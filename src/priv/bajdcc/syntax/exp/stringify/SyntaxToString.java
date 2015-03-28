package priv.bajdcc.syntax.exp.stringify;

import java.util.ArrayList;
import java.util.Stack;

import priv.bajdcc.syntax.ISyntaxComponent;
import priv.bajdcc.syntax.ISyntaxComponentVisitor;
import priv.bajdcc.syntax.exp.BranchExp;
import priv.bajdcc.syntax.exp.OptionExp;
import priv.bajdcc.syntax.exp.PropertyExp;
import priv.bajdcc.syntax.exp.RuleExp;
import priv.bajdcc.syntax.exp.SequenceExp;
import priv.bajdcc.syntax.exp.TokenExp;
import priv.bajdcc.utility.VisitBag;

public class SyntaxToString implements ISyntaxComponentVisitor {

	/**
	 * �ķ��Ƶ�ʽ����
	 */
	private StringBuilder m_Context = new StringBuilder();

	/**
	 * ��Ž����ջ
	 */
	private Stack<ArrayList<String>> m_stkStringList = new Stack<ArrayList<String>>();

	/**
	 * ��ǰ������
	 */
	private ArrayList<String> m_arrData = new ArrayList<String>();

	/**
	 * ����
	 */
	private ISyntaxComponent m_FocusedExp = null;

	/**
	 * LR��Ŀ����'*'
	 */
	private boolean m_bFront = true;

	/**
	 * ��ʼ�����ӽ��
	 */
	private void beginChilren() {
		m_arrData = null;
		m_stkStringList.push(new ArrayList<String>());
	}

	/**
	 * ���������ӽ��
	 */
	private void endChilren() {
		m_arrData = m_stkStringList.pop();
	}

	/**
	 * ������
	 * 
	 * @param exp
	 *            ��ǰ���ʽ���
	 * @param string
	 *            ����
	 */
	private void store(ISyntaxComponent exp, String string) {
		if (m_FocusedExp == exp) {
			/* ���LR��Ŀ������ */
			if (m_bFront) {
				string = "*" + string;
			} else {
				string += "*";
			}
		}
		if (m_stkStringList.isEmpty()) {
			m_Context.append(string);
		} else {
			m_stkStringList.peek().add(string);
		}
	}

	@Override
	public void visitBegin(TokenExp node, VisitBag bag) {
		bag.m_bVisitEnd = false;
		store(node, " `" + node.m_strName + "` ");
	}

	@Override
	public void visitBegin(RuleExp node, VisitBag bag) {
		bag.m_bVisitEnd = false;
		store(node, " " + node.m_strName + " ");

	}

	@Override
	public void visitBegin(SequenceExp node, VisitBag bag) {
		beginChilren();
	}

	@Override
	public void visitBegin(BranchExp node, VisitBag bag) {
		beginChilren();
	}

	@Override
	public void visitBegin(OptionExp node, VisitBag bag) {
		beginChilren();
	}

	@Override
	public void visitBegin(PropertyExp node, VisitBag bag) {
		beginChilren();
	}

	@Override
	public void visitEnd(TokenExp node) {

	}

	@Override
	public void visitEnd(RuleExp node) {

	}

	@Override
	public void visitEnd(SequenceExp node) {
		endChilren();
		StringBuffer sb = new StringBuffer();
		for (String string : m_arrData) {
			sb.append(string);
		}
		store(node, sb.toString());
	}

	@Override
	public void visitEnd(BranchExp node) {
		endChilren();
		StringBuffer sb = new StringBuffer();
		sb.append(" (");
		for (String string : m_arrData) {
			sb.append(string);
			sb.append('|');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(") ");
		store(node, sb.toString());
	}

	@Override
	public void visitEnd(OptionExp node) {
		endChilren();
		store(node, " [" + m_arrData.get(0) + "] ");
	}

	@Override
	public void visitEnd(PropertyExp node) {
		endChilren();
		store(node, m_arrData.get(0)
				+ (node.m_iStorage == -1 ? "" : " [" + node.m_iStorage + "] "));
	}

	@Override
	public String toString() {
		return m_Context.toString();
	}
}
