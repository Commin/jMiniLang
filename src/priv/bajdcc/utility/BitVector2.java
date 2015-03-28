package priv.bajdcc.utility;

import java.util.BitSet;

/**
 * ���ز�������
 *
 * @author bajdcc
 */
public class BitVector2 {
	/**
	 * �ڲ�һ�ز�������
	 */
	BitSet m_BV = null;

	/**
	 * ����
	 */
	int m_nX = 0;

	/**
	 * ����
	 */
	int m_nY = 0;

	public BitVector2(int nx, int ny) {
		if (nx <= 0 || ny <= 0) {
			throw new NegativeArraySizeException();
		}
		m_nX = nx;
		m_nY = ny;
		m_BV = new BitSet(nx * ny);
	}

	private BitVector2() {

	}

	/**
	 * ȫ����λ
	 */
	public void set() {
		m_BV.set(0, m_nX * m_nY - 1);
	}

	/**
	 * ��λ
	 * 
	 * @param x
	 *            ��
	 * @param y
	 *            ��
	 */
	public void set(int x, int y) {
		m_BV.set(x * m_nX + y);
	}

	/**
	 * ��λ
	 * 
	 * @param x
	 *            ��
	 * @param y
	 *            ��
	 * @param value
	 *            ���õ�ֵ
	 */
	public void set(int x, int y, boolean value) {
		m_BV.set(x * m_nX + y, value);
	}

	/**
	 * λ�ò���
	 * 
	 * @param x
	 *            ��
	 * @param y
	 *            ��
	 * @return λ
	 */
	public boolean test(int x, int y) {
		return m_BV.get(x * m_nX + y);
	}

	/**
	 * ȫ������
	 */
	public void clear() {
		m_BV.clear();
	}

	/**
	 * ����
	 * 
	 * @param x
	 *            ��
	 * @param y
	 *            ��
	 */
	public void clear(int x, int y) {
		m_BV.clear(x * m_nX + y);
	}

	@Override
	public Object clone() {
		BitVector2 bv2 = new BitVector2();
		bv2.m_nX = m_nX;
		bv2.m_nY = m_nY;
		bv2.m_BV = (BitSet) m_BV.clone();
		return bv2;
	}
}
