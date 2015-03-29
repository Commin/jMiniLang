package priv.bajdcc.syntax.automata.npa;

import java.util.ArrayList;

import priv.bajdcc.syntax.error.IErrorHandler;

/**
 * ��ȷ���������Զ���������
 * 
 * @author bajdcc
 *
 */
public class NPAEdgeData {
	/**
	 * ������
	 */
	public NPAEdgeType m_Action = NPAEdgeType.MOVE;

	/**
	 * ָ��
	 */
	public NPAInstruction m_Inst = NPAInstruction.PASS;
	
	/**
	 * ָ�����
	 */
	public int m_iIndex = -1;
	
	/**
	 * �������
	 */
	public int m_iHandler = -1;
	
	/**
	 * ״̬����
	 */
	public NPAStatus m_Status = null;
	
	/**
	 * �ǺŲ���
	 */
	public int m_iToken = -1;
	
	/**
	 * LookAhead��
	 */
	public ArrayList<Integer> m_arrLookAhead = null;
	
	/**
	 * ��������
	 */
	public IErrorHandler m_Handler = null;
	
	/**
	 * �������ת��״̬
	 */
	public NPAStatus m_ErrorJump = null;
}
