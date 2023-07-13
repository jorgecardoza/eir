package com.tigo.ea.eir.provider.util;



public class Operaciones {
	public static boolean validarCodigoAVON(String codigo)
	{
		int p1 = 17;
		int p2 = 13;
		int p3 = 11;
		int p4 = 19;
		int p5 = 17;
		int p6 = 13;
		int p7 = 11;
		int cd = 97;
		int cr = 3;
		int sizeCodigo = codigo.length();

		boolean valid = false;
		if (sizeCodigo == 9) {
			long nPon = p1 * Integer.parseInt(codigo.substring(0, 1)) + p2 * Integer.parseInt(codigo.substring(1, 2))
					+ p3 * Integer.parseInt(codigo.substring(2, 3)) + p4 * Integer.parseInt(codigo.substring(3, 4))
					+ p5 * Integer.parseInt(codigo.substring(4, 5)) + p6 * Integer.parseInt(codigo.substring(5, 6))
					+ p7 * Integer.parseInt(codigo.substring(6, 7));

			long nPonDiv = nPon - nPon / cd * cd;
			long nPonDivSum = nPonDiv + cr;
			if (nPonDivSum == Long.parseLong(codigo.substring(7, 9))) {
				valid = true;
			}
		}
		return valid;
	}
}
