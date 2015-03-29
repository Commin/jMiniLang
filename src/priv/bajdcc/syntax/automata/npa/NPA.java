package priv.bajdcc.syntax.automata.npa;

import java.util.ArrayList;
import java.util.Iterator;

import org.vibur.objectpool.ConcurrentLinkedPool;
import org.vibur.objectpool.PoolService;

import priv.bajdcc.syntax.automata.nga.NGA;
import priv.bajdcc.syntax.exp.RuleExp;
import priv.bajdcc.utility.ObjectFactory;

/**
 * <p>
 * <strong>��ȷ���������Զ���</strong>��<b>NPA</b>�������㷨
 * </p>
 * 
 * @author bajdcc
 *
 */
public class NPA extends NGA {

	/**
	 * �߶����
	 */
	private PoolService<NPAEdge> m_EdgesPool = new ConcurrentLinkedPool<NPAEdge>(
			new ObjectFactory<NPAEdge>() {
				public NPAEdge create() {
					return new NPAEdge();
				};
			}, 1024, 10240, false);

	/**
	 * ״̬�����
	 */
	private PoolService<NPAStatus> m_StatusPool = new ConcurrentLinkedPool<NPAStatus>(
			new ObjectFactory<NPAStatus>() {
				public NPAStatus create() {
					return new NPAStatus();
				};
			}, 1024, 10240, false);

	public NPA(ArrayList<RuleExp> nonterminals) {
		super(nonterminals);
		generateNPA();
	}

	/**
	 * ��������״̬
	 * 
	 * @param begin
	 *            ��̬
	 * @param end
	 *            ��̬
	 * @return �µı�
	 */
	protected NPAEdge connect(NPAStatus begin, NPAStatus end) {
		NPAEdge edge = m_EdgesPool.take();// ����һ���±�
		edge.m_Begin = begin;
		edge.m_End = end;
		begin.m_OutEdges.add(edge);// ��ӽ���ʼ�ߵĳ���
		end.m_InEdges.add(edge);// ��ӽ������ߵ����
		return edge;
	}

	/**
	 * �Ͽ�ĳ��״̬��ĳ����
	 * 
	 * @param status
	 *            ĳ״̬
	 * @param edge
	 *            ĳ����
	 */
	protected void disconnect(NPAStatus status, NPAEdge edge) {
		edge.m_End.m_InEdges.remove(edge);// ��ǰ�ߵĽ���״̬����߼���ȥ����ǰ��
		m_EdgesPool.restore(edge);
	}

	/**
	 * �Ͽ�ĳ��״̬�����б�
	 * 
	 * @param begin
	 *            ĳ״̬
	 */
	protected void disconnect(NPAStatus status) {
		/* ���������� */
		for (Iterator<NPAEdge> it = status.m_InEdges.iterator(); it.hasNext();) {
			NPAEdge edge = it.next();
			it.remove();
			disconnect(edge.m_Begin, edge);
		}
		/* ������г��� */
		for (Iterator<NPAEdge> it = status.m_OutEdges.iterator(); it.hasNext();) {
			NPAEdge edge = it.next();
			it.remove();
			disconnect(status, edge);
		}
		m_StatusPool.restore(status);
	}

	/**
	 * ���������Զ���
	 */
	private void generateNPA() {
		
	}
}
