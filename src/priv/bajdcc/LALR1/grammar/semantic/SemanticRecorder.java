package priv.bajdcc.LALR1.grammar.semantic;

import java.util.ArrayList;
import java.util.List;

import priv.bajdcc.LALR1.grammar.error.SemanticException;
import priv.bajdcc.LALR1.grammar.error.SemanticException.SemanticError;
import priv.bajdcc.util.lexer.regex.IRegexStringIterator;
import priv.bajdcc.util.lexer.token.Token;

/**
 * 【语义分析】语义错误记录
 *
 * @author bajdcc
 */
public class SemanticRecorder implements ISemanticRecorder {

	private ArrayList<SemanticException> errors = new ArrayList<>();

	@Override
	public void add(SemanticError error, Token token) {
		errors.add(new SemanticException(error, token));
	}

	@Override
	public List<SemanticException> getErrorList() {
		return errors;
	}

	@Override
	public boolean isCorrect() {
		return errors.isEmpty();
	}

	public String toString(IRegexStringIterator iter) {
		StringBuilder sb = new StringBuilder();
		sb.append("#### 语义错误列表 ####");
		sb.append(System.lineSeparator());
		for (SemanticException error : errors) {
			sb.append(error.toString(iter));
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("#### 语义错误列表 ####");
		sb.append(System.lineSeparator());
		for (SemanticException error : errors) {
			sb.append(error.toString());
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}
}
