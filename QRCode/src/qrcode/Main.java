/*  Authors: 
*	Orfeas Liossatos (oiliossatos@gmail.com)
*	Lionel Pham (https://www.ph4m.online/)
*/

package qrcode;

public class Main 
{

	public static final String INPUT =  "Hello, world!";

	/*
	 * Parameters
	 */
	public static final int VERSION = 4;
	public static final int MASK = 4;
	public static final int SCALING = 20;

	public static void main(String[] args) 
	{

		/*
		 * Encoding
		 */
		boolean[] encodedData = DataEncoding.byteModeEncoding(INPUT, VERSION);

		/*
		 * image
		 */
		int[][] qrCode = MatrixConstruction.renderQRCodeMatrix(VERSION, encodedData);
		
		/*
		 * Visualisation
		 */
		Helpers.show(qrCode, SCALING);
	}

}
