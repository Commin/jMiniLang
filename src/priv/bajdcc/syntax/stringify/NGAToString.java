package priv.bajdcc.syntax.stringify;

import priv.bajdcc.lexer.automata.BreadthFirstSearch;
import priv.bajdcc.syntax.automata.nga.NGAEdge;
import priv.bajdcc.syntax.automata.nga.NGAStatus;
import priv.bajdcc.utility.VisitBag;

/**
 * NGA���л����������������
 * 
 * @author bajdcc
 * @param T
 *            ״̬����
 */
public class NGAToString extends
		BreadthFirstSearch<NGAEdge, NGAStatus> {

	/**
	 * ����
	 */
	private StringBuilder m_Context = new StringBuilder();

	/**
	 * ǰ׺
	 */
	private String m_Prefix = "";

	public NGAToString() {

	}

	public NGAToString(String prefix) {
		m_Prefix = prefix;
	}

	@Override
	public void visitBegin(NGAStatus status, VisitBag bag) {
		/* ���״η��ʽڵ����ȹ���״̬�� */
		if (m_arrStatus.isEmpty()) {
			BreadthFirstSearch<NGAEdge, NGAStatus> bfs = new BreadthFirstSearch<NGAEdge, NGAStatus>();
			status.visit(bfs);
			m_arrStatus = bfs.m_arrStatus;
		}
		/* ���״̬��ǩ */
		appendLine();
		appendPrefix();
		m_Context.append("--== ״̬[" + m_arrStatus.indexOf(status) + "]"
				+ (status.m_Data.m_bFinal ? "[����]" : "") + " ==--");
		appendLine();
		appendPrefix();
		m_Context.append("��ǩ�� " + status.m_Data.m_strLabel);
		appendLine();
		/* ����� */
		for (NGAEdge edge : status.m_OutEdges) {
			appendPrefix();
			m_Context.append("\t���� " + m_arrStatus.indexOf(edge.m_End)
					+ "  ��  ");
			m_Context.append(edge.m_Data.m_Action.getName());
			switch (edge.m_Data.m_Action) {
			case EPSILON:
				break;
			case RULE:
				m_Context.append(" = " + edge.m_Data.m_Rule);
				break;
			case TOKEN:
				m_Context.append(" = " + edge.m_Data.m_Token);
				break;
			default:
				break;
			}
			appendLine();
		}
	}

	/**
	 * ���ǰ׺
	 */
	private void appendPrefix() {
		m_Context.append(m_Prefix);
	}

	/**
	 * �����
	 */
	private void appendLine() {
		m_Context.append(System.getProperty("line.separator"));
	}

	@Override
	public String toString() {
		return m_Context.toString();
	}
}
