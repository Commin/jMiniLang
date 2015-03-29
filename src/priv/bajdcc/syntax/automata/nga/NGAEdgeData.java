package priv.bajdcc.syntax.automata.nga;

import priv.bajdcc.syntax.error.IErrorHandler;
import priv.bajdcc.syntax.exp.RuleExp;
import priv.bajdcc.syntax.exp.TokenExp;

/**
 * ��ȷ�����ķ��Զ���������
 * 
 * @author bajdcc
 *
 */
public class NGAEdgeData {
	/**
	 * ������
	 */
	public NGAEdgeType m_Action = NGAEdgeType.EPSILON;

	/**
	 * �ս������
	 */
	public TokenExp m_Token = null;
	
	/**
	 * ���ս������
	 */
	public RuleExp m_Rule = null;
	
	/**
	 * �洢��ţ�-1Ϊ��Ч��
	 */
	public int m_iStorage = -1;
	
	/**
	 * ��������
	 */
	public IErrorHandler m_Handler = null;
}
