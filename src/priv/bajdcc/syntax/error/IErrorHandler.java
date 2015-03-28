package priv.bajdcc.syntax.error;

import priv.bajdcc.syntax.ISyntaxToken;
import priv.bajdcc.utility.SyntaxErrorBag;

/**
 * �﷨������ӿ�
 *
 * @author bajdcc
 */
public interface IErrorHandler {
	/**
	 * �������
	 * 
	 * @param token
	 *            �Ǻ�
	 * @param bag
	 *            ������Ϣ
	 * @return ������Ϣ
	 */
	public String handle(ISyntaxToken token, SyntaxErrorBag bag);
}
