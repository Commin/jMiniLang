package priv.bajdcc.syntax.automata.nga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.vibur.objectpool.ConcurrentLinkedPool;
import org.vibur.objectpool.PoolService;

import priv.bajdcc.lexer.automata.BreadthFirstSearch;
import priv.bajdcc.syntax.ISyntaxComponent;
import priv.bajdcc.syntax.ISyntaxComponentVisitor;
import priv.bajdcc.syntax.RuleItem;
import priv.bajdcc.syntax.Syntax;
import priv.bajdcc.syntax.exp.BranchExp;
import priv.bajdcc.syntax.exp.OptionExp;
import priv.bajdcc.syntax.exp.PropertyExp;
import priv.bajdcc.syntax.exp.RuleExp;
import priv.bajdcc.syntax.exp.SequenceExp;
import priv.bajdcc.syntax.exp.TokenExp;
import priv.bajdcc.syntax.stringify.NGAToString;
import priv.bajdcc.utility.ObjectFactory;
import priv.bajdcc.utility.VisitBag;

/**
 * <p>
 * <strong>��ȷ�����ķ��Զ���</strong>��<b>NGA</b>�������㷨��<b>AST->NGA</b>��
 * </p>
 * <i>���ܣ�����LR��Ŀ���ļ���</i>
 * 
 * @author bajdcc
 *
 */
public class NGA implements ISyntaxComponentVisitor {

	/**
	 * ���ս������
	 */
	protected ArrayList<RuleExp> m_arrNonTerminals = null;

	/**
	 * ���ս������
	 */
	protected HashMap<RuleItem, NGAStatus> m_mapNGA = new HashMap<RuleItem, NGAStatus>();

	/**
	 * �߶����
	 */
	private PoolService<NGAEdge> m_EdgesPool = new ConcurrentLinkedPool<NGAEdge>(
			new ObjectFactory<NGAEdge>() {
				public NGAEdge create() {
					return new NGAEdge();
				};
			}, 1024, 10240, false);

	/**
	 * ״̬�����
	 */
	private PoolService<NGAStatus> m_StatusPool = new ConcurrentLinkedPool<NGAStatus>(
			new ObjectFactory<NGAStatus>() {
				public NGAStatus create() {
					return new NGAStatus();
				};
			}, 1024, 10240, false);

	/**
	 * �����������ݰ�
	 */
	private NGABag m_Bag = null;

	public NGA(ArrayList<RuleExp> nonterminals) {
		m_arrNonTerminals = nonterminals;
		generateNGAMap();
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
	protected NGAEdge connect(NGAStatus begin, NGAStatus end) {
		NGAEdge edge = m_EdgesPool.take();// ����һ���±�
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
	protected void disconnect(NGAStatus status, NGAEdge edge) {
		edge.m_End.m_InEdges.remove(edge);// ��ǰ�ߵĽ���״̬����߼���ȥ����ǰ��
		m_EdgesPool.restore(edge);
	}

	/**
	 * �Ͽ�ĳ��״̬�����б�
	 * 
	 * @param begin
	 *            ĳ״̬
	 */
	protected void disconnect(NGAStatus status) {
		/* ���������� */
		for (Iterator<NGAEdge> it = status.m_InEdges.iterator(); it.hasNext();) {
			NGAEdge edge = it.next();
			it.remove();
			disconnect(edge.m_Begin, edge);
		}
		/* ������г��� */
		for (Iterator<NGAEdge> it = status.m_OutEdges.iterator(); it.hasNext();) {
			NGAEdge edge = it.next();
			it.remove();
			disconnect(status, edge);
		}
		m_StatusPool.restore(status);
	}

	/**
	 * ����NGAӳ���
	 */
	private void generateNGAMap() {
		for (RuleExp exp : m_arrNonTerminals) {
			int i = 0;
			for (RuleItem item : exp.m_Rule.m_arrRules) {
				/* ���ʽת����NGA */
				m_Bag = new NGABag();
				m_Bag.m_Expression = item.m_Expression;
				m_Bag.m_strPrefix = exp.m_strName + "[" + i + "]";
				m_Bag.m_Expression.visit(this);
				ENGA enga = m_Bag.m_outputNGA;
				/* NGAȥEpsilon�� */
				NGAStatus status = deleteEpsilon(enga);
				/* ���� */
				m_mapNGA.put(item, status);
				i++;
			}
		}
	}

	/**
	 * NGAȥEpsilon�ߣ���DFAȥE���㷨���ƣ�
	 * 
	 * @param enga
	 *            ENGA
	 * @return NGA״̬
	 */
	private NGAStatus deleteEpsilon(ENGA enga) {
		/* ��ȡ״̬�հ� */
		ArrayList<NGAStatus> NGAStatusList = getNGAStatusClosure(
				new BreadthFirstSearch<NGAEdge, NGAStatus>(), enga.m_Begin);
		/* �ɵ���״̬���� */
		ArrayList<NGAStatus> availableStatus = new ArrayList<NGAStatus>();
		/* �ɵ����ǩ���� */
		ArrayList<String> availableLabels = new ArrayList<String>();
		/* �ɵ����ǩ����ϣ�����ڲ��ң� */
		HashSet<String> availableLabelsSet = new HashSet<String>();
		/* ����������Ч״̬ */
		availableStatus.add(NGAStatusList.get(0));
		availableLabels.add(NGAStatusList.get(0).m_Data.m_strLabel);
		availableLabelsSet.add(NGAStatusList.get(0).m_Data.m_strLabel);
		for (NGAStatus status : NGAStatusList) {
			if (status == NGAStatusList.get(0)) {// �ų���һ��
				continue;
			}
			boolean available = false;
			for (NGAEdge edge : status.m_InEdges) {
				if (edge.m_Data.m_Action != NGAEdgeType.EPSILON) {// ����Epsilon��
					available = true;// ��ǰ�ɵ���
					break;
				}
			}
			if (available
					&& !availableLabelsSet.contains(status.m_Data.m_strLabel)) {
				availableStatus.add(status);
				availableLabels.add(status.m_Data.m_strLabel);
				availableLabelsSet.add(status.m_Data.m_strLabel);
			}
		}
		BreadthFirstSearch<NGAEdge, NGAStatus> epsilonBFS = new BreadthFirstSearch<NGAEdge, NGAStatus>() {
			@Override
			public boolean testEdge(NGAEdge edge) {
				return edge.m_Data.m_Action == NGAEdgeType.EPSILON;
			}
		};
		/* ����������Ч״̬ */
		for (NGAStatus status : availableStatus) {
			/* ��ȡ��ǰ״̬��Epsilon�հ� */
			ArrayList<NGAStatus> epsilonClosure = getNGAStatusClosure(
					epsilonBFS, status);
			/* ȥ������״̬ */
			epsilonClosure.remove(status);
			/* ����Epsilon�հ���״̬ */
			for (NGAStatus epsilonStatus : epsilonClosure) {
				if (epsilonStatus.m_Data.m_bFinal) {
					/* ����հ�������̬����ǰ״̬Ϊ��̬ */
					status.m_Data.m_bFinal = true;
				}
				/* �����հ������б� */
				for (NGAEdge edge : epsilonStatus.m_OutEdges) {
					if (edge.m_Data.m_Action != NGAEdgeType.EPSILON) {
						/* ������� */
						int idx = availableLabels
								.indexOf(edge.m_End.m_Data.m_strLabel);
						/* �����ǰ�߲���Epsilon�ߣ��ͽ��հ��е���Ч����ӵ���ǰ״̬ */
						connect(status, availableStatus.get(idx)).m_Data = edge.m_Data;
					}
				}
			}
		}
		/* ɾ��Epsilon�� */
		for (NGAStatus status : NGAStatusList) {
			for (Iterator<NGAEdge> it = status.m_OutEdges.iterator(); it
					.hasNext();) {
				NGAEdge edge = it.next();
				if (edge.m_Data.m_Action == NGAEdgeType.EPSILON) {
					it.remove();
					disconnect(status, edge);// ɾ��Epsilon��
				}
			}
		}
		/* ɾ����Ч״̬ */
		ArrayList<NGAStatus> unaccessiableStatus = new ArrayList<NGAStatus>();
		for (NGAStatus status : NGAStatusList) {
			if (!availableStatus.contains(status)) {
				unaccessiableStatus.add(status);
			}
		}
		for (NGAStatus status : unaccessiableStatus) {
			NGAStatusList.remove(status);// ɾ����Ч״̬
			disconnect(status);// ɾ����״̬�йص����б�
		}
		return enga.m_Begin;
	}

	/**
	 * ��ȡNGA״̬�հ�
	 * 
	 * @param bfs
	 *            �����㷨
	 * @param status
	 *            ��̬
	 * @return ��̬�հ�
	 */
	protected static ArrayList<NGAStatus> getNGAStatusClosure(
			BreadthFirstSearch<NGAEdge, NGAStatus> bfs, NGAStatus status) {
		status.visit(bfs);
		return bfs.m_arrStatus;
	}

	/**
	 * ��ʼ�����ӽ��
	 */
	private void beginChilren() {
		m_Bag.m_childNGA = null;
		m_Bag.m_stkNGA.push(new ArrayList<ENGA>());
	}

	/**
	 * ���������ӽ��
	 */
	private void endChilren() {
		m_Bag.m_childNGA = m_Bag.m_stkNGA.pop();
	}

	/**
	 * ������
	 * 
	 * @param enpa
	 *            EpsilonNGA
	 */
	private void store(ENGA enga) {
		if (m_Bag.m_stkNGA.isEmpty()) {
			enga.m_End.m_Data.m_bFinal = true;
			m_Bag.m_outputNGA = enga;
		} else {
			m_Bag.m_stkNGA.peek().add(enga);
		}
	}

	@Override
	public void visitBegin(TokenExp node, VisitBag bag) {

	}

	@Override
	public void visitBegin(RuleExp node, VisitBag bag) {

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
		/* �½�ENGA */
		ENGA enga = createENGA(node);
		/* ����ENGA�߲����浱ǰ��� */
		NGAEdge edge = connect(enga.m_Begin, enga.m_End);
		edge.m_Data.m_Action = NGAEdgeType.TOKEN;
		edge.m_Data.m_Token = node;
		store(enga);
	}

	@Override
	public void visitEnd(RuleExp node) {
		/* �½�ENGA */
		ENGA enga = createENGA(node);
		/* ����ENGA�߲����浱ǰ��� */
		NGAEdge edge = connect(enga.m_Begin, enga.m_End);
		edge.m_Data.m_Action = NGAEdgeType.RULE;
		edge.m_Data.m_Rule = node;
		store(enga);
	}

	@Override
	public void visitEnd(SequenceExp node) {
		endChilren();
		/* ���� */
		ENGA enga = null;
		for (ENGA child : m_Bag.m_childNGA) {
			if (enga != null) {
				connect(enga.m_End, child.m_Begin);// ��β����
				enga.m_End = child.m_End;
			} else {
				enga = m_Bag.m_childNGA.get(0);
			}
		}
		store(enga);
	}

	@Override
	public void visitEnd(BranchExp node) {
		endChilren();
		/* �½�ENGA */
		ENGA enga = createENGA(node);
		/* ���� */
		for (ENGA child : m_Bag.m_childNGA) {
			/* ���Ʊ�ǩ */
			child.m_Begin.m_Data.m_strLabel = enga.m_Begin.m_Data.m_strLabel;
			child.m_End.m_Data.m_strLabel = enga.m_End.m_Data.m_strLabel;
			/* ������β */
			connect(enga.m_Begin, child.m_Begin);
			connect(child.m_Begin, enga.m_End);
		}
		store(enga);
	}

	@Override
	public void visitEnd(OptionExp node) {
		endChilren();
		/* ���Ψһ��һ���ӽ�� */
		ENGA enga = m_Bag.m_childNGA.get(0);
		enga.m_Begin.m_Data.m_strLabel = Syntax.getSingleString(
				m_Bag.m_strPrefix, m_Bag.m_Expression, node, true);
		enga.m_End.m_Data.m_strLabel = Syntax.getSingleString(
				m_Bag.m_strPrefix, m_Bag.m_Expression, node, false);
		/* ��ӿ�ѡ�ߣ���Epsilon�� */
		connect(enga.m_Begin, enga.m_End);
		store(enga);
	}

	@Override
	public void visitEnd(PropertyExp node) {
		endChilren();
		/* ���Ψһ��һ���ӽ�� */
		ENGA enga = m_Bag.m_childNGA.get(0);
		enga.m_Begin.m_Data.m_strLabel = Syntax.getSingleString(
				m_Bag.m_strPrefix, m_Bag.m_Expression, node, true);
		enga.m_End.m_Data.m_strLabel = Syntax.getSingleString(
				m_Bag.m_strPrefix, m_Bag.m_Expression, node, false);
		/* ��øý��ı� */
		NGAEdge edge = enga.m_Begin.m_OutEdges.get(0);
		edge.m_Data.m_iStorage = node.m_iStorage;
		edge.m_Data.m_Handler = node.m_ErrorHandler;
		store(enga);
	}

	/**
	 * �½�ENGA
	 * 
	 * @param node
	 *            ���
	 * @return ENGA��
	 */
	private ENGA createENGA(ISyntaxComponent node) {
		ENGA enga = new ENGA();
		enga.m_Begin = m_StatusPool.take();
		enga.m_End = m_StatusPool.take();
		enga.m_Begin.m_Data.m_strLabel = Syntax.getSingleString(
				m_Bag.m_strPrefix, m_Bag.m_Expression, node, true);
		enga.m_End.m_Data.m_strLabel = Syntax.getSingleString(
				m_Bag.m_strPrefix, m_Bag.m_Expression, node, false);
		return enga;
	}

	/**
	 * ��ȷ�����ķ��Զ�������
	 */
	public String getNGAString() {
		StringBuffer sb = new StringBuffer();
		for (NGAStatus status : m_mapNGA.values()) {
			sb.append(getNGAString(status, ""));
			sb.append(System.getProperty("line.separator"));
		}
		return sb.toString();
	}

	/**
	 * ��ȷ�����ķ��Զ�������
	 * 
	 * @param status
	 *            NGA״̬
	 * @param prefix
	 *            ǰ׺
	 * @return ����
	 */
	public String getNGAString(NGAStatus status, String prefix) {
		NGAToString alg = new NGAToString(prefix);
		status.visit(alg);
		return alg.toString();
	}

	@Override
	public String toString() {
		return getNGAString();
	}
}
