package priv.bajdcc.lexer.automata.nfa;

import java.util.ArrayList;
import java.util.HashSet;

import priv.bajdcc.lexer.automata.BreadthFirstSearch;

/**
 * NFA״̬
 * 
 * @author bajdcc
 *
 */
public class NFAStatus {
	/**
	 * ���߼���
	 */
	public ArrayList<NFAEdge> m_OutEdges = new ArrayList<NFAEdge>();

	/**
	 * ��߼���
	 */
	public ArrayList<NFAEdge> m_InEdges = new ArrayList<NFAEdge>();

	/**
	 * ����
	 */
	public NFAStatusData m_Data = new NFAStatusData();

	/**
	 * ���ڱ���������״̬���ڵ�����״̬����ͨ������������PATH��
	 * 
	 * @param bfs
	 *            �����㷨
	 */
	public void visit(BreadthFirstSearch<NFAEdge, NFAStatus> bfs) {
		ArrayList<NFAStatus> stack = bfs.m_Path;
		HashSet<NFAStatus> set = new HashSet<NFAStatus>();
		stack.clear();
		set.add(this);
		stack.add(this);
		for (int i = 0; i < stack.size(); i++) {// ����ÿ��״̬
			NFAStatus status = stack.get(i);
			bfs.visitBegin(status);
			for (NFAEdge edge : status.m_OutEdges) {// ����״̬�ĳ���
				if (!set.contains(edge.m_End) && bfs.testEdge(edge)) {// ��δ�����ʣ��ұ����ͷ���Ҫ��
					stack.add(edge.m_End);
					set.add(edge.m_End);
				}
			}
			bfs.visitEnd(status);
		}
	}
}
