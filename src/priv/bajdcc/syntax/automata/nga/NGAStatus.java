package priv.bajdcc.syntax.automata.nga;

import java.util.ArrayList;
import java.util.HashSet;

import priv.bajdcc.lexer.automata.BreadthFirstSearch;
import priv.bajdcc.utility.VisitBag;

/**
 * ��ȷ�����ķ��Զ���״̬
 * 
 * @author bajdcc
 *
 */
public class NGAStatus {
	/**
	 * ���߼���
	 */
	public ArrayList<NGAEdge> m_OutEdges = new ArrayList<NGAEdge>();

	/**
	 * ��߼���
	 */
	public ArrayList<NGAEdge> m_InEdges = new ArrayList<NGAEdge>();

	/**
	 * ����
	 */
	public NGAStatusData m_Data = new NGAStatusData();

	/**
	 * ���ڱ���������״̬���ڵ�����״̬����ͨ������������PATH��
	 * 
	 * @param bfs
	 *            �����㷨
	 */
	public void visit(BreadthFirstSearch<NGAEdge, NGAStatus> bfs) {
		ArrayList<NGAStatus> stack = bfs.m_arrStatus;
		HashSet<NGAStatus> set = new HashSet<NGAStatus>();
		stack.clear();
		set.add(this);
		stack.add(this);
		for (int i = 0; i < stack.size(); i++) {// ����ÿ��״̬
			NGAStatus status = stack.get(i);
			VisitBag bag = new VisitBag();
			bfs.visitBegin(status, bag);
			if (bag.m_bVisitChildren) {
				for (NGAEdge edge : status.m_OutEdges) {// ����״̬�ĳ���
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
