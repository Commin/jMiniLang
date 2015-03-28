package priv.bajdcc.syntax.error;

import priv.bajdcc.utility.Position;

/**
 * �ķ����ɹ����е��쳣
 * 
 * @author bajdcc
 *
 */
@SuppressWarnings("serial")
public class SyntaxException extends Exception {

	/**
	 * �ķ��Ƶ�ʽ���������еĴ���
	 */
	public enum SyntaxError {
		NULL("�Ƶ�ʽΪ��"), UNDECLARED("�޷�ʶ��ķ���"), SYNTAX("�﷨����"), INCOMPLETE(
				"�Ƶ�ʽ������"), EPSILON("���ܲ����մ�"), INDIRECT_RECURSION("���ڼ����ݹ�"), FAILED(
				"���ܲ����ַ���");

		private String message;

		SyntaxError(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	};

	public SyntaxException(SyntaxError error, Position pos, Object obj) {
		super(error.getMessage());
		m_Position = pos;
		m_kError = error;
		if (obj != null) {
			m_strInfo = obj.toString();
		}
	}

	/**
	 * λ��
	 */
	private Position m_Position = new Position();

	/**
	 * @return ����λ��
	 */
	public Position getPosition() {
		return m_Position;
	}

	/**
	 * ��������
	 */
	private SyntaxError m_kError = SyntaxError.NULL;

	/**
	 * @return ��������
	 */
	public SyntaxError getErrorCode() {
		return m_kError;
	}

	/**
	 * ������Ϣ
	 */
	private String m_strInfo = "";

	/**
	 * @return ������Ϣ
	 */
	public String getInfo() {
		return m_strInfo;
	}
}
