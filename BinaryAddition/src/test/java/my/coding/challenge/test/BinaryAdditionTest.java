package my.coding.challenge.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import my.coding.challenge.BinaryAddition;

import org.junit.Test;

/**
 * 
 * @author Richard Mason
 * Test class to unit test BinaryAddition.java 
 *
 */
public class BinaryAdditionTest {
	BinaryAddition ba = new BinaryAddition();
	
	@Test
	public void testValidInput1() {
		assertEquals("Pass expected", "100", ba.add("10", "10"));
	}
	
	@Test
	public void testValidInput2() {
		assertEquals("Pass expected", "1000100110", ba.add("111110100", "110010"));
	}
	
	@Test
	public void testValidInput3() {
		assertEquals("Pass expected", 
				"110100111110110101111000110", 
				ba.add("101111101011110000011111111", 
				"101010011000101011000111"));
	}
	
	@Test
	public void testValidInput4() {
		assertEquals("Pass expected",
				"111001011001011010110111101100001100011001000011110001110001100110000010110010011100111111001101101100010100101011110110100000000000000000000001", 
				ba.add("11100101100101101011011110110000110001100100001111000111000110010110110110011100110011010000010111010000000000000000000000000000000000000000000", 
				"11100101100101101011011110110000110001100100001111000111000110011001011111110110110100101001010110010010100101011110110100000000000000000000001"));
	}
	
	@Test
	public void testIncorrectResult1() {
		assertEquals("Failure expected", "100", ba.add("10", "11"));
	}
	
	@Test
	public void testIncorrectResult2() {
		assertNotEquals("Failure expected", "101", ba.add("11", "10"));
	}
	
	@Test(expected = IllegalArgumentException.class) 
	public void testInvalidArguments1() {
		ba.add("12345", "11"); // Pass expected
	}
	
	@Test(expected = IllegalArgumentException.class) 
	public void testInvalidArguments2() {
		ba.add("11", "12345"); // Pass expected
	}
	
	// main created to assist proving solution
	public static void main(String[] args) {
		if (args.length == 3) {
			BinaryAddition ba = new BinaryAddition();
			String result = ba.add(args[0], args[1]);
			System.out.println(result);
			System.out.println(args[2].equals(result));
		} else {
			System.out.println("Terminating...received "+ args.length + " argument(s) when expecting 2.");
		}
	}
}
