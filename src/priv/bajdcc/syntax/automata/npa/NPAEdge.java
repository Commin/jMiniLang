package priv.bajdcc.syntax.automata.npa;

/**
 * ��ȷ���������Զ�����
 * 
 * @author bajdcc
 *
 */
public class NPAEdge {
	/**
	 * ��̬
	 */
	public NPAStatus m_Begin;

	/**
	 * ��̬
	 */
	public NPAStatus m_End;

	/**
	 * ����
	 */
	public NPAEdgeData m_Data = new NPAEdgeData();
}
