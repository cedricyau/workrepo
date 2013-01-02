package musi.interfaces.bsu.requestbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import musi.interfaces.bsu.pojo.Security;

public abstract class RequestBuilder {
	protected List<Security> securities;
	protected List<String> fields;
	private String securityFile;
	private String fieldFile;
	private static final Pattern ISIN_PATTERN = Pattern.compile("[A-Z]{2}([A-Z0-9]){9}[0-9]");

	public RequestBuilder(String securityFile, String fieldFile) {
		this.securityFile = securityFile;
		this.fieldFile = fieldFile;
	}

	public void buildRequest() {};

	public void addSecurities() {
		InputStreamReader is = null;
		BufferedReader br = null;

		try {
			is = new InputStreamReader(new FileInputStream(new File(securityFile)));
			br = new BufferedReader(is);
			securities = new ArrayList<Security>();
			String line;
			
			IsinValidator isinValidator = new IsinValidator();
			CusipValidator cusipValidator = new CusipValidator();
			
			while ((line = br.readLine()) != null) {
				Security security = new Security();
				security.setId(line);
				
				if (isinValidator.checkIsinCode(line.trim())) {
					security.setIdType("ISIN");
				} else if (cusipValidator.checkCusipCode(line.trim())) {
					security.setIdType("CUSIP");
				} else {
					security.setIdType("UNKNOWN");
				}
				
				securities.add(security);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public void addFields() {
		InputStreamReader is = null;
		BufferedReader br = null;

		try {
			is = new InputStreamReader(new FileInputStream(new File(fieldFile)));
			br = new BufferedReader(is);
			fields = new ArrayList<String>();
			String line;

			while ((line = br.readLine()) != null) {
				fields.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	class IsinValidator {
		public boolean checkIsinCode(final String isin) {
			if (isin == null) {
				return false;
			}
	
			if (!ISIN_PATTERN.matcher(isin).matches()) {
				return false;
			}
	
			StringBuilder digits = new StringBuilder();
			for (int i = 0; i < 11; ++i) {
				digits.append(Character.digit(isin.charAt(i), 36));
			}
			digits.reverse();
	
			int sum = 0;
			for (int i = 0; i < digits.length(); ++i) {
				int digit = Character.digit(digits.charAt(i), 36);
				if (i % 2 == 0) {
					digit *= 2;
				}
				sum += digit / 10;
				sum += digit % 10;
			}
	
			int checkDigit = Character.digit(isin.charAt(11), 36);
			int tensComplement = (sum % 10 == 0) ? 0 : ((sum / 10) + 1) * 10 - sum;
	
			return checkDigit == tensComplement;
		}
	}

	class CusipValidator {
		public boolean checkCusipCode(String ccNum) {

			char number[] = ccNum.toCharArray();

			int len = number.length;
			int sum = 0;
			for (int i = 0; i < len - 1; i++) {
				int num = mapChar(number[i]);

				// Double all the odd digits
				if (i % 2 != 0)
					num *= 2;

				// Combine digits.  i.e., 16 = (1 + 6) = 7
				if (num > 9)
					num = (num % 10) + (num / 10);
				sum += num;
			}

			int checkDigit = mapChar(number[number.length - 1]);

			// This is the mathmatical modulus - not the remainder.  i.e., 10 mod 7 = 3
			int mod = (10 - (sum % 10)) % 10;
			if (mod == checkDigit) {
				return true;
			}

			return false;
		}

		/**
		 * Standard & Poor's maps A..Z to 10..35 
		 * @param c
		 * @return numeric value of the letter
		 */
		private int mapChar(char c) {
			if (c >= '0' && c <= '9')
				return c - '0';
			return c - 'A' + 10;
		}
	}
}
