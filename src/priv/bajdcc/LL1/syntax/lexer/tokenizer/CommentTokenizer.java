package priv.bajdcc.LL1.syntax.lexer.tokenizer;

import priv.bajdcc.util.lexer.algorithm.TokenAlgorithm;
import priv.bajdcc.util.lexer.algorithm.filter.StringPairFilter;
import priv.bajdcc.util.lexer.error.RegexException;
import priv.bajdcc.util.lexer.token.MetaType;
import priv.bajdcc.util.lexer.token.Token;
import priv.bajdcc.util.lexer.token.TokenType;

/**
 * 注释解析
 * 
 * @author bajdcc
 *
 */
public class CommentTokenizer extends TokenAlgorithm {

	public CommentTokenizer() throws RegexException {
		super(getRegexString(), new StringPairFilter(MetaType.LT, MetaType.GT));
	}

	public static String getRegexString() {
		return "<.*>";
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see
	 * priv.bajdcc.lexer.algorithm.ITokenAlgorithm#getToken(java.lang.String,
	 * priv.bajdcc.lexer.token.Token)
	 */
	@Override
	public Token getToken(String string, Token token) {
		token.kToken = TokenType.COMMENT;
		token.object = string.trim();
		return token;
	}
}
