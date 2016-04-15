package my.coding.challenge;

/**
 * 
 * @author Richard Mason
 * Class to output the result of adding two binary numbers
 *
 */
public class BinaryAddition {
	
	/**
	 * 
	 * @param num1
	 * @param num2
	 * @return result
	 * @throws IllegalArgumentException
	 */
	public String add(String num1, String num2) throws IllegalArgumentException {
		StringBuilder result = new StringBuilder();
		String inner;
		String outer;
		if (num1.length() == num2.length()) {
			outer = num1;
			inner = num2;
		} else {
			inner = num1.length() > num2.length() ? num1 : num2;
			outer = num1.length() < num2.length() ? num1 : num2;
		}
		int carry = 0;
		for (int j = outer.length() - 1; j >= 0;) {
			for (int i = inner.length() - 1; i >= 0;) {
				if (inner.charAt(i) != '1' && inner.charAt(i) != '0') {
					throw new IllegalArgumentException(inner);	
				}
				if (j >= 0 && (outer.charAt(j) != '1' && (outer.charAt(j) != '0'))) {
					throw new IllegalArgumentException(outer);
				}
				if (inner.charAt(i) == '0') {
					if (j >= 0 && outer.charAt(j) == '0') {
						if (carry == 1) {
							result.insert(0, "1");
							carry = 0;
						} else {
							result.insert(0, "0");
						}
					} else if (j >= 0 && outer.charAt(j) == '1') {
						if (carry == 1) {
							result.insert(0, "0");
						} else {
							result.insert(0, "1");
						}						
					} else {
						if (carry == 1) {
							result.insert(0, "1");
							carry = 0;
						}
					}
				} else if (inner.charAt(i) == '1') {
					if (j >= 0 && outer.charAt(j) == '0') {
						if (carry == 1) {
							result.insert(0, "0");
						} else {
							result.insert(0, "1");
						}
					} else if (j >= 0 && outer.charAt(j) == '1') {
						if (carry == 0) {
							result.insert(0, "0");
							carry = 1;
						} else {
							result.insert(0, "1");
							carry = 1;
						}
					} else {
						if (carry == 1) {
							result.insert(0, "0");
						} else {
							result.insert(0, "1");
						}
					}
				}
				i--;
				j--;
			}
			if (carry == 1) {
				result.insert(0, carry);
			}
		}
		return result.toString();
	}
}