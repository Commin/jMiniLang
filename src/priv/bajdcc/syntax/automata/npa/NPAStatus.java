package priv.bajdcc.syntax.automata.npa;

import java.util.ArrayList;
import java.util.HashSet;

import priv.bajdcc.lexer.automata.BreadthFirstSearch;
import priv.bajdcc.utility.VisitBag;

/**
 * ��ȷ���������Զ���״̬
 * 
 * @author bajdcc
 *
 */
public class NPAStatus {
	/**
	 * ���߼���
	 */
	public ArrayList<NPAEdge> m_OutEdges = new ArrayList<NPAEdge>();

	/**
	 * ��߼���
	 */
	public ArrayList<NPAEdge> m_InEdges = new ArrayList<NPAEdge>();

	/**
	 * ����
	 */
	public NPAStatusData m_Data = new NPAStatusData();

	/**
	 * ���ڱ���������״̬���ڵ�����״̬����ͨ������������PATH��
	 * 
	 * @param bfs
	 *            �����㷨
	 */
	public void visit(BreadthFirstSearch<NPAEdge, NPAStatus> bfs) {
		ArrayList<NPAStatus> stack = bfs.m_arrStatus;
		HashSet<NPAStatus> set = new HashSet<NPAStatus>();
		stack.clear();
		set.add(this);
		stack.add(this);
		for (int i = 0; i < stack.size(); i++) {// ����ÿ��״̬
			NPAStatus status = stack.get(i);
			VisitBag bag = new VisitBag();
			bfs.visitBegin(status, bag);
			if (bag.m_bVisitChildren) {
				for (NPAEdge edge : status.m_OutEdges) {// ����״̬�ĳ���
					if (!set.contains(edge.m_End) && bfs.testEdge(edge)) {// ��δ�����ʣ��ұ����ͷ���Ҫ��
						stack.add(edge.m_End);
						set.add(edge.m_End);
					}
				}
			}
			if (bag.m_bVisitEnd) {
				bfs.visitEnd(status);
			}
		}
	}
}
