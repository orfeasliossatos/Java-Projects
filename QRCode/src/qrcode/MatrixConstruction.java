/*  Authors: 
*	Orfeas Liossatos (oiliossatos@gmail.com)
*	Lionel Pham (https://www.ph4m.online/)
*/

package qrcode;

public class MatrixConstruction 
{
	/*
	 * Constants defining the color in ARGB format
	 * 
	 * W = White integer for ARGB
	 * 
	 * B = Black integer for ARGB
	 * 
	 * both needs to have their alpha component to 255
	 */
	public static final int B = 0xFF_00_00_00; // The color black
	public static final int W = 0xFF_FF_FF_FF; // The color white

	// ...  MYDEBUGCOLOR = ...;
	// feel free to add your own colors for debugging purposes
	public static final int G = 0xFF_00_FF_00; // The color green
	public static final int R = 0xFF_FF_00_00; // The color green
	public static final int T = 0x00_00_00_00; // The color (transparent)

	/*
	 * Enumerators to be used in the program
	 */
	enum direction {HORIZONTALLY, VERTICALLY};
	
	/**
	 * Create the matrix of a QR code with the given data.
	 * 
	 * @param version
	 *            The version of the QR code
	 * @param data
	 *            The data to be written on the QR code
	 * @param mask
	 *            The mask used on the data. If not valid (e.g: -1), then no mask is
	 *            used.
	 * @return The matrix of the QR code
	 */
	public static int[][] renderQRCodeMatrix(int version, boolean[] data, int mask) 
	{
		/*
		 * PART 2
		 */
		int[][] matrix = constructMatrix(version, mask);
		
		/*
		 * PART 3
		 */
		addDataInformation(matrix, data, mask);

		return matrix;
	}

	/*
	 * =======================================================================
	 * 
	 * ****************************** PART 2 *********************************
	 * 
	 * =======================================================================
	 */

	/*
	 * Constants defining various patterns that are to be placed on the empty matrix. 
	 * 
	 * Finder pattern and alignment patterns are for position and orientation
	 * 
	 * Vertical and horizontal separators isolate the finder patterns from the rest of the matrix.
	 * 
	 */

	// Finder Pattern
	public static final int [][] finderPattern = 
	{
		{B,B,B,B,B,B,B},
		{B,W,W,W,W,W,B},
		{B,W,B,B,B,W,B},
		{B,W,B,B,B,W,B},
		{B,W,B,B,B,W,B},
		{B,W,W,W,W,W,B},
		{B,B,B,B,B,B,B},	
	};
	
	// Alignement Pattern
	public static final int [][] alignementPattern = 
	{
		{B,B,B,B,B},
		{B,W,W,W,B},
		{B,W,B,W,B},
		{B,W,W,W,B},
		{B,B,B,B,B},
			
	};
	
	// Horizontal Separator
	public static final int [][] vertSeparator = 
	{
		{W,W,W,W,W,W,W,W},		
	};
	
	// Vertical Separator
	public static final int [][] horiSeparator = 
	{
		{W},{W},{W},{W},{W},{W},{W},{W},
	};
	
	/**
	 * Create a matrix (2D array) ready to accept data for a given version and mask
	 * 
	 * @param version
	 *            the version number of QR code (has to be between 1 and 4 included)
	 * @param mask
	 *            the mask id to use to mask the data modules. Has to be between 0
	 *            and 7 included to have a valid matrix. If the mask id is not
	 *            valid, the modules would not be not masked later on, hence the
	 *            QRcode would not be valid
	 * @return the qrcode with the patterns and format information modules
	 *         initialized. The modules where the data should be remain empty.
	 */
	public static int[][] constructMatrix(int version, int mask) 
	{
		// Step 1 : Create a matrix of correct size
		int[][] matrix = initializeMatrix(version);
		
		// Step 2 : Add finder patterns and separators to all but the bottom-right corner of the matrix 
		addFinderPatterns(matrix);

		// Step 3 : Add an alignement pattern to the bottom-right corner of the matrix
		addAlignmentPatterns(matrix, version);
		
		// Step 4 : Add two timing patterns to the matrix
		addTimingPatterns(matrix);
		
		// Step 5 : Add a single black module at a specific position
		addDarkModule(matrix);

		// Step 6 : Add format information around the finder patterns
		addFormatInformation(matrix, mask);
		
		return matrix;
	}

	/**
	 * Create an empty 2d array of integers of the size needed for a QR code of the
	 * given version
	 * 
	 * @param version
	 *            the version number of the qr code (has to be between 1 and 4
	 *            included
	 * @return an empty matrix
	 */
	public static int[][] initializeMatrix(int version) 
	{
		int size = QRCodeInfos.getMatrixSize(version);
		
		int[][] newMatrice = new int[size][size];
		
		return newMatrice;
	}
	
	/**
	 * This function copies the contents of a 2D array (the pattern) into another (larger) 
	 * 2D array (the matrix) at the indices posX, posY.
	 * 
	 * @param posX The x coordinate of matrix at which the contents of the pattern are copied
	 * @param posY The y coordinate of matrix at which the contents of the pattern are copied
	 * @param pattern The pattern to be copied
	 * @param matrix The matrix to which we copy the pattern
	 */
	private static void placePattern(int[][] pattern, int[][] matrix, int posX, int posY)
	{
		// ASSUMING the pattern fits into the matrix...
		
		// Loop through columns and and rows of the pattern 
		for (int x = 0; x < pattern.length; ++x)
		{
			for (int y = 0; y < pattern[x].length; ++y)
			{
				matrix[posX + x][posY + y] = pattern[x][y]; // Start drawing the top-left corner of the pattern at posX, posY.
			}
		}
		
	}

	/**
	 * Add all finder patterns to the given matrix with a border of White modules.
	 * 
	 * @param matrix
	 *            the 2D array to modify: where to add the patterns
	 */
	public static void addFinderPatterns(int[][] matrix) 
	{
		// Consider the matrix has an "X" axis and a "Y" axis. 
		// X goes left to right
		// Y goes top to bottom
		
		// Top-left finder pattern and separators at position (0,0)
		int topLeftX = 0; 
		int topLeftY = 0;
		
		placePattern(finderPattern, matrix, topLeftX, topLeftY);						// Finder Pattern
		placePattern(vertSeparator, matrix, topLeftX + finderPattern.length, topLeftY); // Vertical Seperator
		placePattern(horiSeparator, matrix, topLeftX, topLeftY + finderPattern.length); // Horizontal Seperator
		
		// Top-right finder pattern and separators at position (matrix.length - finderPattern.length, 0)
 		int hautDroitX = matrix.length - finderPattern.length; 
		int hautDroitY = 0;
		
		placePattern(finderPattern, matrix, hautDroitX, hautDroitY);							// Finder Pattern
		placePattern(vertSeparator, matrix, hautDroitX - 1, hautDroitY);						// Vertical Seperator
		placePattern(horiSeparator, matrix, hautDroitX - 1, hautDroitY + finderPattern.length); // Horizontal Seperator
		
		// Bottom-left finder pattern and separators at position (0, matrix.length - finderPattern.length)
		int basGaucheX = 0; 
		int basGaucheY = matrix.length - finderPattern.length;
		
		placePattern(finderPattern, matrix, basGaucheX, basGaucheY);							// Finder Pattern
		placePattern(vertSeparator, matrix, basGaucheX + finderPattern.length, basGaucheY - 1); // Vertical Seperator
		placePattern(horiSeparator, matrix, basGaucheX, basGaucheY - 1);						// Horizontal Seperator
	}

	/**
	 * Add the alignment pattern if needed, does nothing for version 1
	 * 
	 * @param matrix
	 *            The 2D array to modify
	 * @param version
	 *            the version number of the QR code needs to be between 1 and 4
	 *            included
	 */
	public static void addAlignmentPatterns(int[][] matrix, int version) 
	{
		// As long as the version number is strictly larger than 1
		if (version > 1) 
		{
			int distanceFromCorner = 9; // The top-left corner of the alignement pattern is always placed 9 squares from the bottom-right corner
			
			placePattern(alignementPattern, matrix, matrix.length - distanceFromCorner, matrix.length - distanceFromCorner); 
		}
	}

	/**
	 * Add the timings patterns
	 * 
	 * @param matrix
	 *            The 2D array to modify
	 */
	public static void addTimingPatterns(int[][] matrix) 
	{
		// Generating timing patterns (cannot be predefined like finder patterns) 
		
		int timingPatternLength = matrix.length - ((finderPattern.length + 1) * 2); // The timing pattern fits exactly between the two finder patterns and two seperators
		
		int [] timingPattern = new int[timingPatternLength];
		
		// Timing patterns alternate between black and white
		for (int i = 0; i < timingPatternLength; ++i) 
		{
			if (i % 2 == 0) // If i is even
			{
				timingPattern[i] = B;
			}
			else // Otherwise, if i is odd
			{
				timingPattern[i] = W;
			}
		}
		
		// Placement of the vertical timing pattern
		for (int j = 0; j < timingPattern.length; ++j) 
		{
			matrix[finderPattern.length - 1][j + finderPattern.length + 1] = timingPattern[j];
		}
		
		// Placement of the horizontal timing pattern
		for (int j = 0; j < timingPattern.length; ++j) 
		{
			matrix[j + finderPattern.length + 1][finderPattern.length - 1] = timingPattern[j];
		}
	}

	/**
	 * Add the dark module to the matrix
	 * 
	 * @param matrix
	 *            the 2-dimensional array representing the QR code
	 */
	public static void addDarkModule(int[][] matrix) 
	{
		// Add a single dark module at exactly this position
		matrix [finderPattern.length + 1][matrix.length - (finderPattern.length + 1)] = B;
	}

	/**
	 * Add the format information to the matrix
	 * 
	 * @param matrix
	 *            the 2-dimensional array representing the QR code to modify
	 * @param mask
	 *            the mask id
	 */
	public static void addFormatInformation(int[][] matrix, int mask) 
	{
		// Conversion from booleans to a sequence of ints (black and white squares)
		boolean[] formatSequence = QRCodeInfos.getFormatSequence(mask);
		
		int [] bitSequence = new int[formatSequence.length];
		
		for (int i = 0; i < formatSequence.length; ++i) 
		{
			bitSequence[i] = formatSequence[i] ? B : W;
		}
		
		// Placing the format information alongside the finder patterns. 
		// X and Y "axis" as defined in addFinderPatterns method.
		
		int startX = 0;					// Start drawing the horizontal format information at x = 0
		int startY = matrix.length - 1; // Start drawing the vertical format information at y = matrix.length - 1
		
		// The format bits are to be placed at specific (symetric) locations, and are to skip certain rows and columns
		
		int timingPatternLength = matrix.length - ((finderPattern.length + 1) * 2); // The length of the timing pattern
		
		int timingPatternXY = 6; // The horizontal / vertical timing pattern is located in row 6 / column 6.
		
		int formatBitsXY = 8;	// The horizontal / vertical format bits are to be placed in row 8 / column 8.

		int jumpAfterBit = 6;	// Add the timing length to startX and startY once we hit format bit 6.
		
		// Continue placing format bits until the edge of the matrix is reached.
		for (int i = 0; startX + i < matrix.length; ++i) 
		{
			// -- Horizontal format bits --
			
			// If we hit the vertical timing pattern, skip a module
			if (i == timingPatternXY) 
			{
				startX++;
			}
			
			// Place the module
			matrix[startX + i][formatBitsXY] = bitSequence[i];
			
			// Jump, now start drawing beneath the top-right finder pattern
			if (i == jumpAfterBit)
			{
				startX += timingPatternLength;
			}
			
			
			// -- Vertical format bits -- 
			
			// If we hit the horizontal timing pattern, skip a module
			if (startY - i == timingPatternXY)
			{
				startY--;
			}
			
			// Place the module
			matrix[formatBitsXY][startY - i] = bitSequence[i];
			
			// Jump, start drawing next to the top-left finder pattern
			if (i == jumpAfterBit)
			{
				startY -= timingPatternLength;
			}
		}
		
	}

	/*
	 * =======================================================================
	 * ****************************** PART 3 *********************************
	 * =======================================================================
	 */

	/**
	 * Choose the color to use with the given coordinate using the masking 0
	 * 
	 * @param col
	 *            x-coordinate
	 * @param row
	 *            y-coordinate
	 * @param color
	 *            : initial color without masking
	 * @return the color with the masking
	 */
	public static int maskColor(int col, int row, boolean dataBit, int masking) 
	{
		int x = col;	// X "axis" from left to right
		int y = row;	// Y "axis" from top to bottom
		
		int module = dataBit ? B : W; // If dataBit is true, module is black, otherwise it's white.
		
		// Setting up the the masks
		boolean maskedModule = false;
		switch(masking)
		{
		case 0: { maskedModule = ((x + y) % 2 == 0); break;}
		case 1: { maskedModule = (y % 2 == 0); break;}
		case 2: { maskedModule = (x % 3 == 0); break;}
		case 3: { maskedModule = ((x + y) % 3 == 0); break;}
		case 4: { maskedModule = (((int)(Math.floor(y / 2) + (int)Math.floor(x / 3)) % 2 == 0)); break;}
		case 5: { maskedModule = (((x * y) % 2) + ((x * y) % 3) == 0); break;}
		case 6: { maskedModule = ((((x * y) % 2) + ((x * y) % 3)) % 2 == 0); break;}
		case 7: { maskedModule = ((((x + y) % 2) + ((x * y) % 3)) % 2 == 0); break;}
		}

		// Applying the mask
		if (maskedModule)
		{
			if (module == B) 
			{
				module = W;
			}
			else 
			{
				module = B;
			}
		}
 		return module; 
	}

	/** Checks to see if the entry at position posX, posY is coloured in.
	 * 
	 * @param posX
	 * 			The x position of the matrix
	 * @param posY
	 * 			The y position of the matrix
	 * @param matrix
	 *			The matrix whose entry we're exploring
	 * @return a boolean that is true when that position is coloured in.
	 */
	private static boolean isActivated(int[][] matrix, int posX, int posY)
	{
		boolean isActivated;
		
		if (matrix[posX][posY] == 0)
		{
			isActivated = false;
		}
		else
		{
			isActivated = true;
		}
		
		return isActivated;
	}
	
	/**
	 * This method implements the "zig-zag" motion required in adding data information to the matrix.
	 * @param matrix
	 * 			The matrix to which the data is added
	 * @param data
	 * 			The encoded message
	 * @param x
	 * 			The current x position in the matrix
	 * @param y
	 * 			The current y position in the matrix
	 * @param dataIndex
	 * 			Which boolean in the matrix should be placed
	 * @param mask
	 * 			
	 */
	private static void zigZagAddData(int[][] matrix,  boolean data[], int x, int y, int[] dataIndex, int mask) 
	{
		for (int z = 0; z <= 1; z++) // The zigzag loop shifts the x coordinate one to the left
		{
			if (!isActivated(matrix, x - z, y)) // If no module is already placed (finder patterns, etc)
			{
				if (data.length == 0) // If there is no message to place, put false
				{
					matrix[x - z][y] = maskColor(x - z, y, false, mask);
				}
				else if (dataIndex[0] >= data.length) // If there is no more message to place, put false 
				{
					matrix[x - z][y] = maskColor(x - z, y, false, mask);
				}
				else // Otherwise, place the message as usual
				{
					matrix[x - z][y] = maskColor(x - z, y, data[dataIndex[0]], mask);
					++dataIndex[0]; 
				}
			}
		}
	}
	
	/**
	 * Add the data bits into the QR code matrix
	 * 
	 * @param matrix
	 *            a 2-dimensionnal array where the bits needs to be added
	 * @param data
	 *            the data to add
	 */
	public static void addDataInformation(int[][] matrix, boolean[] data, int mask) 
	{	
		int[] dataIndex = new int[1]; // This evolved type can be passed "by reference" to the method "zigZagAddData"
		dataIndex[0] = 0;
		
		boolean rising = true; // A flag that communicates whether we should be writing upwards or downwards through the matrix
		
		for (int x = matrix.length - 1; x >= 0; x -= 2) // Go from right to left
		{
			if (x == 6) // Skip the vertical timing pattern
			{
				x--;
			}
			
			if (rising) // If writing upwards
			{	
				for (int y = matrix.length - 1; y >= 0; y--)
				{
					zigZagAddData(matrix, data, x, y, dataIndex, mask);
				}
			}
			else // Otherwise, if writing downwards
			{
				for (int y = 0; y < matrix.length; y++) 
				{
					zigZagAddData(matrix, data, x, y, dataIndex, mask);
				}
			}
			
			rising = !rising; // Once we reach the top / bottom of the matrix, now go the other way.
		}
	}

	/*
	 * =======================================================================
	 * 
	 * ****************************** BONUS **********************************
	 * 
	 * =======================================================================
	 */

	/**
	 * Create the matrix of a QR code with the given data.
	 * 
	 * The mask is computed automatically so that it provides the least penalty
	 * 
	 * @param version
	 *            The version of the QR code
	 * @param data
	 *            The data to be written on the QR code
	 * @return The matrix of the QR code
	 */
	public static int[][] renderQRCodeMatrix(int version, boolean[] data) {

		int mask = findBestMasking(version, data);

		return renderQRCodeMatrix(version, data, mask);
	}

	/**
	 * Find the best mask to apply to a QRcode so that the penalty score is
	 * minimized. Compute the penalty score with evaluate
	 * 
	 * @param data
	 * @return the mask number that minimize the penalty
	 */
	public static int findBestMasking(int version, boolean[] data) 
	{
		int bestMask = 0;
		int penaltyScore = 0;
		int lowestPenaltyScore = 9999999; // Set a high number by default so subsequent score must be lower than the first
		
		// Try all 8 masks out (0 to 7)
		for (int mask = 0; mask < 8; mask++)
		{
			// Construct and add information to the matrix
			int[][] matrix = constructMatrix(version, mask);
			
			addDataInformation(matrix, data, mask);
			
			// Calculate the penalty score of the matrix
			penaltyScore = evaluate(matrix);
			
			// Determine if this mask is better than the previous ones
			if (penaltyScore < lowestPenaltyScore)
			{
				bestMask = mask;
				lowestPenaltyScore = penaltyScore;
			}
		}
		
		return bestMask;
	}
	
	/**
	 * Count the number of consecutive modules along a certain direction in a matrix, where the minimum number of consecutive modules 
	 * that are counted is specified.
	 * 
	 * @param matrix
	 * 			the matrix whose consecutive modules are to be counted
	 * @param dir
	 * 			the modules are to be counted horizontally or vertically
	 * @param minConsecutiveModules
	 * 			there must be this many consecutive modules for those to be added up in the total 
	 * @return
	 * 			the number of consecutive modules
	 */
	private static int countConsecutiveModules(int[][] matrix, direction dir, int minConsecutiveModules)
	{
		int penaltyScore = 0; 	// Type 1 penalty score to be returned
		
		int prevColour = T;		// Stores the previous colour that was looked at
		int currColour = T;		// Stores the current colour that is being looked at
		
		int nbConsecutiveModules = 1;	// Counts how many consecutive same-colour modules have been counted before swapping to another colour
		
		// Loop through the matrix
		for (int y = 0; y < matrix.length; y++) 
		{
			for (int x = 0; x < matrix.length; x++)
			{	
				// The meaning of x and y is swapped depending on whether we want to count horizontal or vertical cons. modules
				if (dir == direction.HORIZONTALLY)
				{
					currColour = matrix[y][x];
				}
				else if (dir == direction.VERTICALLY)
				{
					currColour = matrix[x][y];
				}
				
				// Check if the current and previous colours are the same
				if (prevColour == currColour)
				{
					++nbConsecutiveModules;
				}
				else // If they're not, reset the count to 1
				{
					nbConsecutiveModules = 1;
				}
				prevColour = currColour; // Next loop we want prevColour to refer to the colour we're currently considering
				
				// If there are exactly minConsecutiveModules consecutive modules, add that number to our count
				if (nbConsecutiveModules == minConsecutiveModules)
				{
					penaltyScore += minConsecutiveModules;
				}
				else if (nbConsecutiveModules > minConsecutiveModules) // Any than that more simply adds 1
				{
					penaltyScore++;
				}
			}
			prevColour = T; 			// We don't want same-colour count to carry over to the next row or column
			nbConsecutiveModules = 1;	// Reset our count
		}
			
		return penaltyScore;
	}

	/**
	 * Counts the number of same-colour 2x2 squares of modules. The squares may superimpose each other.
	 * Then adds three penalty score per square counted.
	 * 
	 * @param matrix
	 * 			the matrix whose 2x2 squares of modules are to be counted;
	 * @return
	 * 			the penalty score determined by the number of squares
	 */
	private static int count2x2Squares(int[][] matrix)
	{
		int penaltyScore = 0; // Type 2 penalty score to be returned
		
		// Loop through the matrix, stopping 1 before the bottom and right sides
		for (int x = 0; x < matrix.length - 1; x++)
		{
			for (int y = 0; y < matrix.length - 1; y++)
			{
				// x and y are the top-left corner of the 2x2 square
				if ((matrix[x][y] == matrix[x][y + 1]) 
						&& (matrix[x][y + 1] == matrix[x + 1][y]) 
						&& (matrix[x + 1][y] == matrix[x + 1][y + 1])) // Only three comparisons needed because equality is a transitive relation
				{
					penaltyScore += 3; // Add three penalty score per same-colour 2x2 square
				}
			}
		}
	
		return penaltyScore; 
	}
	
	/**
	 * Searches for a specified illegal sequence along a certain direction. Returns 40 times the number of such sequences found
	 * in the matrix as a penalty score.
	 * @param matrix
	 * @param sequence
	 * 			The illegal sequence
	 * @param dir
	 * 			Search horizontally or vertically
	 * @return
	 * 		the penalty score determined by the number of illegal sequences found in the matrix
	 */
	private static int countIllegalSequence(int[][] matrix, int[] sequence, direction dir)
	{
		int penaltyScore = 0; // Penalty score type 3
		
		int[][] whiteBorderMatrix = new int[matrix.length + 2][matrix.length + 2]; // Add space for a white border (+1 on each side)
		
		// If the sequence cannot fit in the white border matrix, return 0
		if (sequence.length > whiteBorderMatrix.length) 
		{
			return 0;
		}
		
		// Fill the white border matrix with the contents of matrix, with white around the borders
		for (int y = 0; y < whiteBorderMatrix.length; y++) 
		{
			for (int x = 0; x < whiteBorderMatrix.length; x++)
			{
				// If a border position, add white
				if ((x == 0) || (y == 0) || (x == whiteBorderMatrix.length - 1) || (y == whiteBorderMatrix.length - 1))
				{
					whiteBorderMatrix[y][x] = W;
				}
				else // Otherwise, add the contents of matrix
				{
					whiteBorderMatrix[y][x] = matrix[y - 1][x - 1]; // Position (1, 1) of wBMatrix takes contents of position (0, 0) of matrix
				}		
			}
		}
		
		// Search for the sequence in the matrix, horizontally or vertically
		boolean containsSequence = true; // By default we assume that the row / column contains the sequence.
		switch(dir)
		{
			case HORIZONTALLY : 
			{
				for (int y = 0; y < whiteBorderMatrix.length; y++)
				{
					for (int x = 0; x < whiteBorderMatrix.length - sequence.length + 1; x++) // The horizontal sequence must be able to fit between x and the edge of the matrix.
					{
						for (int j = x, k = 0; j < x + sequence.length; j++, k++) // Start at x, look at the sequence.length next positions
						{
							if (whiteBorderMatrix[y][j] != sequence[k])
							{
								containsSequence = false;
							}
						}
						
						if (containsSequence)
						{
							penaltyScore += 40;
						}
						containsSequence = true; // By default we assume true
					}
				}
				break;
			}
			case VERTICALLY : 
			{
				for (int y = 0; y < whiteBorderMatrix.length - sequence.length + 1; y++) // The vertical sequence must be able to fit between y and the edge of the matrix
				{
					for (int x = 0; x < whiteBorderMatrix.length; x++)
					{
						for (int j = y, k = 0; j < y + sequence.length; j++, k++) // Start at y, look at the sequence.length next positions
						{
							if (whiteBorderMatrix[j][x] != sequence[k])
							{
								containsSequence = false;
							}
						}
						
						if (containsSequence)
						{
							penaltyScore += 40;
						}
						containsSequence = true; // By default we assume true
					}
				}
				break;
			}
		}
		return penaltyScore;
	}
	
	/**
	 * Computes penalty score according to a formula. 
	 * @param matrix
	 * @return
	 * 		the penalty score determined by the formula
	 */
	private static int penaltyFormula(int[][] matrix)
	{
		int penaltyScore = 0;
		
		// Total number of modules
		int nModules = matrix.length * matrix.length; // Square formula
		
		// Total number of black modules
		int bModules = 0;
		for (int y = 0; y < matrix.length; y++)
		{
			for (int x = 0; x < matrix.length; x++)
			{
				if (matrix[y][x] == B)
				{
					bModules++;
				}
			}
		}
		
		// Percentage of black modules
		double blackModulesPercent = ((double) bModules / nModules) * 100;
		
		// Find closest multiples of 5
		int lowerMultiple = ((int) Math.floor(blackModulesPercent)) - ((int) blackModulesPercent % 5);
		int upperMultiple = lowerMultiple + 5;
		
		// Subtract fifty, then take absolute values of upper and lower multiple of 5
		lowerMultiple = Math.abs(lowerMultiple - 50);
		upperMultiple = Math.abs(upperMultiple - 50);
		
		// Find the smaller of both numbers. The penalty score is equal to 2x that number.
		if (lowerMultiple < upperMultiple)
		{
			penaltyScore = 2 * lowerMultiple;
		}
		else
		{
			penaltyScore = 2 * upperMultiple;
		}
		
		return penaltyScore;
	}
	
	/**
	 * Compute the penalty score of a matrix
	 * 
	 * @param matrix:
	 *            the QR code in matrix form
	 * @return the penalty score obtained by the QR code, lower the better
	 */
	public static int evaluate(int[][] matrix) 
	{
		int penaltyScore = 0;
		
		// PENALTY TYPE 1 : If there are at least 3 modules of the same colour in a row/col : +1 penalty score for each consecutive module
		penaltyScore += countConsecutiveModules(matrix, direction.HORIZONTALLY, 3);
		penaltyScore += countConsecutiveModules(matrix, direction.VERTICALLY, 3);
		
		// PENALTY TYPE 2 : For every 2x2 square of same-colour modules : +3 penalty score
		penaltyScore += count2x2Squares(matrix);
		
		// PENALTY TYPE 3 : Search for sequences : +40 penalty score
		int[] sequence1 = {W, W, W, W, B, W, B, B, B, W, B};
		int[] sequence2 = {B, W, B, B, B, W, B, W, W, W, W};
		
		penaltyScore += countIllegalSequence(matrix, sequence1, direction.HORIZONTALLY);
		penaltyScore += countIllegalSequence(matrix, sequence1, direction.VERTICALLY);
		penaltyScore += countIllegalSequence(matrix, sequence2, direction.HORIZONTALLY);
		penaltyScore += countIllegalSequence(matrix, sequence2, direction.VERTICALLY);
		
		// PENALTY TYPE 4 : Formula
		penaltyScore += penaltyFormula(matrix);
		
		return penaltyScore;
	}
}
