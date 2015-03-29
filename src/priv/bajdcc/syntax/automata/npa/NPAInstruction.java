package priv.bajdcc.syntax.automata.npa;

/**
 * ��ȷ���������Զ���ָ��
 *
 * @author bajdcc
 */
public enum NPAInstruction {
	PASS("ͨ��"), READ("����"), SHIFT("�ƽ�"), TRANSLATE("����"), LEFT_RECURSION("��ݹ�"), TRANSLATE_DISCARD(
			"��������"), LEFT_RECURSION_DISCARD("������ݹ�"), TRANSLATE_FINISH("�������");

	private String name;

	NPAInstruction(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
