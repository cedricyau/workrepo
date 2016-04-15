package my.coding.challenge.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import my.coding.challenge.BinaryAddition;

import org.junit.Test;

public class BinaryAdditionTest {
	BinaryAddition ba = new BinaryAddition();
	
	@Test
	public void testValidInput1() {
		assertEquals("Testing sum of 2 small binary numbers", "100", ba.add("10", "10"));
	}
	
	@Test
	public void testValidInput2() {
		assertEquals("1000100110", ba.add("111110100", "110010"));
	}
	
	@Test
	public void testValidInput3() {
		assertEquals("110100111110110101111000110", 
				ba.add("101111101011110000011111111", 
				"101010011000101011000111"));
	}
	
	@Test
	public void testValidInput4() {
		assertEquals("111001011001011010110111101100001100011001000011110001110001100110000010110010011100111111001101101100010100101011110110100000000000000000000001", 
				ba.add("11100101100101101011011110110000110001100100001111000111000110010110110110011100110011010000010111010000000000000000000000000000000000000000000", 
				"11100101100101101011011110110000110001100100001111000111000110011001011111110110110100101001010110010010100101011110110100000000000000000000001"));
	}
	
	@Test
	public void testInvalidResult1() {
		String result = ba.add("10", "11"); // expect result of 101
		assertEquals("101", result);
		assertNotEquals("100", result);
	}
	
	@Test
	public void testInvalidArguments() {
		
	}
	
	public static void main(String[] args) {
		if (args.length == 2) { 
			new BinaryAddition().add(args[0], args[1]);
		} else {
			System.out.println("Terminating...received "+ args.length + " argument(s) when expecting 2.");
		}
	}
}
