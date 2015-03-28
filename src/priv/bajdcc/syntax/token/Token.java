package priv.bajdcc.syntax.token;

import priv.bajdcc.utility.Position;

/**
 * ����
 * 
 * @author bajdcc
 */
public class Token {

	/**
	 * ��������
	 */
	public TokenType m_kToken = TokenType.ERROR;

	/**
	 * ����
	 */
	public Object m_Object = null;

	/**
	 * λ��
	 */
	public Position m_Position = new Position();

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%04d,%03d:\t%-10s\t%s", m_Position.m_iLine,
				m_Position.m_iColumn, m_kToken.getName(),
				m_Object == null ? "(null)" : m_Object.toString()));
		return sb.toString();
	}

	public static Token transfer(priv.bajdcc.lexer.token.Token token) {
		Token tk = new Token();
		tk.m_Object = token.m_Object;
		tk.m_Position = token.m_Position;
		switch (token.m_kToken) {
		case COMMENT:
			tk.m_kToken = TokenType.COMMENT;
			break;
		case EOF:
			tk.m_kToken = TokenType.EOF;
			break;
		case ERROR:
			tk.m_kToken = TokenType.ERROR;
			break;
		case ID:
			tk.m_kToken = TokenType.NONTERMINAL;
			break;
		case MACRO:
			tk.m_kToken = TokenType.HANDLER;
			break;
		case OPERATOR:
			tk.m_kToken = TokenType.OPERATOR;
			break;
		case STRING:
			tk.m_kToken = TokenType.TERMINAL;
			break;
		case WHITESPACE:
			tk.m_kToken = TokenType.WHITSPACE;
			break;
		default:
			break;
		}
		return tk;
	}
}