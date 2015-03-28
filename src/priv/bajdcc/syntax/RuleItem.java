package priv.bajdcc.syntax;

import java.util.ArrayList;

import priv.bajdcc.syntax.exp.RuleExp;
import priv.bajdcc.syntax.exp.TokenExp;

/**
 * �ķ����򲿼����ķ��Ƶ�ʽ��
 *
 * @author bajdcc
 */
public class RuleItem {

	/**
	 * ������ʽ
	 */
	public ISyntaxComponent m_Expression = null;

	/**
	 * First���ϣ��ս����
	 */
	public ArrayList<TokenExp> m_arrFirstSetTokens = new ArrayList<TokenExp>();

	/**
	 * First���ϣ����ս����
	 */
	public ArrayList<RuleExp> m_arrFirstSetRules = new ArrayList<RuleExp>();

	/**
	 * �����ָ��
	 */
	public Rule m_Parent = null;

	/**
	 * �Ƿ���Ч
	 */
	public boolean m_bEnable = true;

	public RuleItem(ISyntaxComponent exp, Rule parent) {
		this.m_Expression = exp;
		this.m_Parent = parent;
	}
}
