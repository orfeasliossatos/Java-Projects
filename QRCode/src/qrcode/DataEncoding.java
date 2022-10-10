/*  Authors: 
*	Orfeas Liossatos (oiliossatos@gmail.com)
*	Lionel Pham (https://www.ph4m.online/)
*/

package qrcode;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import reedsolomon.ErrorCorrectionEncoding;

public final class DataEncoding {

	/**
	 * @param input
	 * @param version
	 * @return
	 */
	public static boolean[] byteModeEncoding(String input, int version) 
	{
		// Step 0 : Let version determine maximum length of the input as well as final length of the message.
		final int MAX_LENGTH = QRCodeInfos.getMaxInputLength(version);	
		final int FINAL_LENGTH = QRCodeInfos.getCodeWordsLength(version);
		final int ECC_LENGTH = QRCodeInfos.getECCLength(version);		
		
		// Step 1 : Encode String input to ISO-8859-1
		int[] inputBytes = encodeString(input, MAX_LENGTH);
		
		// Step 2 : Add encoding and length information.
		int[] addedBytes = addInformations(inputBytes);
		
		// Step 3 : Fill sequence with alternating values 236 and 17 until we hit specified length.
		int[] filledBytes = fillSequence(addedBytes, FINAL_LENGTH);
		
		// Step 4 : Add error correction to the message
		int[] errorCorrectedBytes = addErrorCorrection(filledBytes, ECC_LENGTH);
		
		// Step 5. Conversion from bytes to binary
		boolean[] outputBits = bytesToBinaryArray(errorCorrectedBytes);
		
		return outputBits;
	}

	/**
	 * Encode the message to ISO-8859-1
	 * @param input
	 *            The string to convert to ISO-8859-1
	 * @param maxLength
	 *          The maximal number of bytes to encode (will depend on the version of the QR code) 
	 * @return A array that represents the input in ISO-8859-1. The output is
	 *         truncated to fit the version capacity
	 */
	public static int[] encodeString(String input, int maxLength) 
	{	
		// We assume maxLength is a nonzero positive integer
		
		// The input is converted to an array of bytes according to the char (character) set ISO-8859-1
		byte[] tabByte = input.getBytes(StandardCharsets.ISO_8859_1);
		
		// We must now convert the bytes into an integer string.
		
		// The integer string may be as long as maxLength, but no longer.
		int shorterLength = (tabByte.length < maxLength)? tabByte.length : maxLength; 
		
		int[] tabInt = new int[shorterLength];
		
		// Fill tabInt with the contents of inputBytes, converted to positive integers.
		for (int i = 0; i < shorterLength; i++)
		{
			tabInt[i] = Byte.toUnsignedInt(tabByte[i]);
		}
		
		return tabInt; 
	} 
	
	/**
	 * Add the 12 bits information data and concatenate the bytes to it
	 * 
	 * @param inputBytes
	 *            the data byte sequence
	 * @return The input bytes with an header giving the type and size of the data
	 */
	public static int[] addInformations(int[] inputBytes) 
	{
		// Instantiate a new array and fill it with the contents of inputBytes. 
		// This array must have two more slots to make room for the byte mode and the length of the message.
		int[] outputBytes = Arrays.copyOf(inputBytes, inputBytes.length + 2);
		
		// Prefix the message with the relevant byte mode : 0b0100
		int byteMode = 0b0100;
		for (int i = outputBytes.length - 1; i >= 0; i--)
		{
			// Shift the byte to the right by 4 bits
			outputBytes[i] >>= 4;
		
			// If the byte is the first in the array, right-merge with 0b0100, 
			if (i == 0)
			{
				outputBytes[i] = merge(byteMode, outputBytes[i]);
			}
			else // otherwise, merge with the first 4 bits of the previous element (reading right to left)
			{
				int firstFourBits = mask(outputBytes[i - 1], 0b1111);
				outputBytes[i] = merge(firstFourBits, outputBytes[i]);
			}
		}
		
		// Add the length of the message in binary after the byte mode : Example :  14 -> 0000_1110
		int messageLength = inputBytes.length;
		for (int i = outputBytes.length - 1; i >= 0; i--)
		{
			// For all but the first and second bytes, shift the byte to the right by 8 bits (in other words, move it to the next index to the right)
			if (i > 1)
			{
				outputBytes[i] = outputBytes[i - 1];
			}
			else if ( i == 1) // Otherwise, merge the first 4 bits of messageLength with last 4 bits of the first byte in inputBytes.
			{
				outputBytes[i] = merge(mask(messageLength, 0b1111), mask(outputBytes[i - 1], 0b1111));
			}
			else if (i == 0) // Otherwise, merge the byte mode with the last 4 bits of message length.
			{
				outputBytes[i] = mask(outputBytes[i], 0b1111_0000) | (messageLength >> 4);
			}
		}
		
		return outputBytes;
	}
	
	/**
	 * This function takes unsigned bytes (in fact, integers) of length 4 each, and concatenates them.
	 * For example, merge(0b1100, 0b0110) is equal to 0b1100_0110
	 * @param firstInt
	 * 			the first integer, will form the leftmost part of the concatenation
	 * @param secondInt
	 * 			the second integer, will form the rightmost part of the concatenation
	 * @return An integer whose value is given by the concatenation of the two parameters in binary form.
	 */
	private static int merge(int firstInt, int secondInt)
	{
		return (firstInt << 4) | secondInt;
	}
	
	/** This function takes integers and applies a mask to their binary representation.
	 * Binary digits under the mask are conserved, the others are switched off.
	 * For example, mask(0b0110_1010, 0b0000_1111) is equal to 0b0000_1010.
	 * @param intToMask
	 * 			the integer whose binary representation will undergo masking
	 * @param mask
	 * 			the integer whose binary representation will act as a mask.
	 * @return
	 */
	private static int mask(int intToMask, int mask)
	{
		return intToMask & mask;
	}
	
	/**
	 * Add padding bytes to the data until the size of the given array matches the
	 * finalLength
	 * 
	 * @param encodedData
	 *            the initial sequence of bytes
	 * @param finalLength
	 *            the minimum length of the returned array
	 * @return an array of length max(finalLength,encodedData.length) padded with
	 *         bytes 236,17
	 */
	public static int[] fillSequence(int[] encodedData, int finalLength) 
	{
		// If the encodedData is already long enough, return encodedData unchanged.
		if (finalLength <= encodedData.length)
		{
			return encodedData;
		}
		else // Otherwise fill an array with the contents of encodedData, then alternating 236's and 17's (starting at 236)
		{
			int[] filledData = new int[finalLength]; // The array to be filled
			
			for (int i = 0, k = 0; i < filledData.length; i++) // k starts counting up only after finishing copying from encodedData
			{
				if (i >= encodedData.length)
				{
					filledData[i] = (k % 2 == 0)? 236 : 17; // Alternating between 236 and 17 by determining whether k is even or odd.
					k++;
				}
				else
				{
					filledData[i] = encodedData[i];
				}
			}
			
			return filledData;
		}
	}
	
	/**
	 * Add the error correction to the encodedData
	 * 
	 * @param encodedData
	 *            The byte array representing the data encoded
	 * @param eccLength
	 *            the version of the QR code
	 * @return the original data concatenated with the error correction
	 */
	public static int[] addErrorCorrection(int[] encodedData, int eccLength) 
	{
		// If no error correction data is added, return encodedData unchanged.
		if (eccLength == 0)
		{
			return encodedData;
		}
		else // Otherwise, use Reed-Solomon and append the error correction data to the end of encodedData.
		{
			int[] correctedData = Arrays.copyOf(encodedData, encodedData.length + eccLength);
			
			int[] errorCorrectionData = ErrorCorrectionEncoding.encode(encodedData, eccLength);
			
			for (int k = 0; k < errorCorrectionData.length; k++) // Fill the next eccLength slots with error correction data
			{
				correctedData[k + encodedData.length] = errorCorrectionData[k];
			}
			
			return correctedData;
		}
	}

	
	/**
	 * Encode the byte array into a binary array represented with boolean using the
	 * most significant bit first.
	 * 
	 * @param data
	 *            an array of bytes
	 * @return a boolean array representing the data in binary
	 */
	public static boolean[] bytesToBinaryArray(int[] data) 
	{
		// Array to store the binary values
		boolean[] binaryArray = new boolean[data.length * Byte.SIZE];
		
		// Intermediary to be assigned true or false
		boolean isActivated;
		
		// Loop through every bit, and determine whether it is activated.
		for (int i = 0; i < data.length; i++) // For every byte in data
		{
			for (int k = Byte.SIZE - 1, j = 0; k >= 0; k--, j++) // For every bit in byte data[i], starting from the most significant digit
			{
				if (mask(data[i], (0b1 << k)) > 0) // This sliding mask determines whether or not bit k is active (larger than 0)
				{
					isActivated = true;
				}
				else 
				{
					isActivated = false;
				}
				
				binaryArray[Byte.SIZE * i + j] = isActivated; // The expression inside the [] resolves to every integer between 0 to the total number of bits. 
			}
		}
		
		return binaryArray;
	}
}
