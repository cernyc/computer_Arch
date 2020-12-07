import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Scanner;

public class sim {
	
	//common variables
	public static String address;
	public static String state;
	public static int predictTaken = 0;
	public static int predictNotTaken = 0;
	public static int numprediction = 0;
	public static int mispredicted = 0;
	public static int goodprediction = 0;
	public static int generalState = 0;
	
	//Binodal/GShared variables
	public static String binString;
	static String temp = "";
	public static String BHR = "";
	public static int BHRValue = 0;

	// GShare variables
	public static int gpcBits;
	public static int gnumBits;
	public static int bpcBits;
	public static int bnumBits;
	public static int x;
	public static int y;
	public static int[] gdefaultArray;
	public static int[] bdefaultArray;
	public static int[] chooser;
	public static String bBHR = "";
	public static String gBHR = "";
	public static int gBHRValue = 0;
	public static int bBHRValue = 0;
	public static int chooserBHRValue = 0;
	public static boolean bispredictedTaken;
	public static boolean gispredictedTaken;
	public static boolean ispredictedTaken;

	
	public static void main(String[] args) {
		
		 String type = args[0];
		 
		 if (type.equals("smith")){
			 GeneralnBit(args);
		 }
		 if (type.equals("bimodal")){
			 Generalbimodal(args);
		 }
		 if (type.equals("gshare")){
			 GeneralGshared(args);
		 }
		 if (type.equals("hybrid")){
			 GeneralHybrid(args);
		 }
	}
	
	public static void GeneralnBit(String[] args) {

		String file = args[2];		 
		int numBits = Integer.parseInt(args[1]);
		int counter = powerOf2(numBits)/2;

		Scanner scanner;
		try {
			scanner = new Scanner(new File(file));
			scanner.useDelimiter(" ");
			while (scanner.hasNextLine()) {
				String s[] = scanner.nextLine().split(" ");
				address = s[0];
				state = s[1];
				numprediction++;
				boolean ispredictedTaken;
					// check if we are predicting it
					if (counter > (powerOf2(numBits)/2)-1) {
						//we predict the branch to be taken
						ispredictedTaken = true;
						predictTaken++;
					}
					else {
						//we predict the branch to not be taken
						ispredictedTaken = false;
						predictNotTaken++;
					}
					// increment or decrement the value
					if (state.equals("T") || state.equals("t")) {
						//check if we have a misorediction	
						if (!ispredictedTaken) {
							mispredicted ++;
						}
						else {
							goodprediction++;
						}
						// check if it's a max state
						if (counter < powerOf2(numBits)-1 ) {
							counter++;
						}
					} else {
						if (ispredictedTaken) {
							mispredicted ++;
						}
						else {
							goodprediction++;
						}
						// check if it's a min state
						if (counter > 0) {
							counter--;
						}
					}				
			}
			System.out.println("number of predictions: " + numprediction);
			System.out.println("number of mispredictions: " + mispredicted);
			NumberFormat formatter = new DecimalFormat("#0.00");
			double rate = 0.0;
			rate = ((double)mispredicted)/((double)numprediction);
			System.out.println("Misprediction rate: " + formatter.format(rate*100)+"%");
			System.out.println("FINAL COUNTER CONTENT: "+counter);
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void Generalbimodal(String[] args) {

		int numBits = Integer.parseInt(args[1]);
		String file = args[2];		 
		int pcBits = 3;
		Scanner scanner;
		int[] defaultArray = new int[powerOf2(numBits)];
		boolean ispredictedTaken;
		// String bitValue = Integer.toBinaryString(numBits);
		// System.out.println(bitValue);
		int x = (int)Math.ceil(numBits/4.0)+1;
		for (int i = 0; i < numBits; i++) {
			BHR = BHR + "0";
		}
		for (int i = 0; i < powerOf2(numBits); i++) {
			defaultArray[i] = 4;
		}
		// System.out.println("Power of 2: "+ powerOf2(numBits));
		// System.out.println("We have an default array: "+ Arrays.toString(defaultArray));
		try {
			scanner = new Scanner(new File(file));
			scanner.useDelimiter(" ");
			// while (scanner.hasNextLine()) {
			while (scanner.hasNextLine()) {
				String s[] = scanner.nextLine().split(" ");
				address = s[0];
				state = s[1];
				temp = "";
				String newtemp = "";
				//binString = convertStringToBinary(address);
				for (int i =(address.length()-x); i<address.length(); i ++) {
					temp = temp+address.charAt(i);
				}
				String binAd = Integer.toBinaryString(Integer.parseInt(temp, 16));
				while (binAd.length() <= numBits+2) {
					binAd = "0"+binAd;
				}
				numprediction++;
				for (int i = (binAd.length()-numBits-2); i<binAd.length()-2; i++) {
					newtemp = newtemp + binAd.charAt(i);
				}
				while (newtemp.length() < numBits) {
					newtemp = "0"+newtemp;
				}
				//System.out.println(" address is : "+address);
				//System.out.println(" temp is : "+temp);
				//System.out.println("New temp is : "+newtemp);
				// create a value for arrays
				BHRValue = Integer.parseInt(newtemp,2);
				// check if we are predicting it
				if (defaultArray[BHRValue] > 3) {
					// we predict the branch to be taken
					ispredictedTaken = true;
					predictTaken++;
				} else {
					// we predict the branch to not be taken
					ispredictedTaken = false;
					predictNotTaken++;
				}
				// increment or decrement the value
				if (state.equals("T") || state.equals("t")) {
					// check if we have a misorediction
					if (!ispredictedTaken) {
						mispredicted++;
					}
					// check if it's a max state
					if (defaultArray[BHRValue] < 7) {
						defaultArray[BHRValue] = defaultArray[BHRValue] + 1;
					}
					BHR = shiftLeft(BHR);
					BHR = BHR + "1";
				} else {
					if (ispredictedTaken) {
						mispredicted++;
					}
					// check if it's a min state
					if (defaultArray[BHRValue] > 0) {
						defaultArray[BHRValue] = defaultArray[BHRValue]-1;
					}
					BHR = shiftLeft(BHR);
					BHR = BHR + "0";
				}

			}
			System.out.println("number of predictions: " + numprediction);
			System.out.println("number of mispredictions: " + mispredicted);
			NumberFormat formatter = new DecimalFormat("#0.00");
			double rate = 0.0;
			rate = ((double)mispredicted)/((double)numprediction);
			System.out.println("Misprediction rate: " + formatter.format(rate*100)+"%");
			System.out.println("FINAL BIMODAL CONTENT: ");
			// System.out.println("goodprediction " + goodprediction);
			// System.out.println("sum " + (goodprediction+mispredicted));
			for (int i = 0; i < defaultArray.length; i++) {
				System.out.println(i + " : " + defaultArray[i]);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void GeneralGshared(String[] args) {

		int pcBits = Integer.parseInt(args[1]);
		int numBits = Integer.parseInt(args[2]);
		String file = args[3];		

		Scanner scanner;
		int[] defaultArray = new int[powerOf2(pcBits)];
		boolean ispredictedTaken;
		
		int x = (int)Math.ceil(pcBits/4.0)+1;
		for (int i = 0; i < numBits; i++) {
			BHR = BHR + "0";
		}
		for (int i = 0; i < powerOf2(pcBits); i++) {
			defaultArray[i] = 4;
		}
		// System.out.println("Power of 2: "+ powerOf2(numBits));
		// System.out.println("We have an default array: "+ Arrays.toString(defaultArray));
		try {
			scanner = new Scanner(new File(file));
			scanner.useDelimiter(" ");
			while (scanner.hasNextLine()) {
				String s[] = scanner.nextLine().split(" ");
				address = s[0];
				state = s[1];
				temp = "";
				String newtemp = "";
				String mn = "";
				String xored = "";
				String concat = "";

				for (int i =(address.length()-x); i<address.length(); i ++) {
					temp = temp+address.charAt(i);
				}
				String binAd = Integer.toBinaryString(Integer.parseInt(temp, 16));
				while (binAd.length() <= pcBits+2) {
					binAd = "0"+binAd;
				}
				numprediction++;
				for (int i = (binAd.length()-pcBits-2); i<binAd.length()-numBits-2; i++) {
					mn = mn + binAd.charAt(i);
				}
				while (mn.length() < pcBits-numBits) {
					mn = "0"+mn;
				}
				for (int i = (binAd.length()-numBits-2); i<binAd.length()-2; i++) {
					newtemp = newtemp + binAd.charAt(i);
				}
				while (newtemp.length() < numBits) {
					newtemp = "0"+newtemp;
				}
				xored = XOR(newtemp, BHR);
				concat = mn+xored;

				// create a value for arrays
				BHRValue = Integer.parseInt(concat,2);
				// check if we are predicting it
				if (defaultArray[BHRValue] > 3) {
					// we predict the branch to be taken
					ispredictedTaken = true;
					predictTaken++;
				} else {
					// we predict the branch to not be taken
					ispredictedTaken = false;
					predictNotTaken++;
				}
				// increment or decrement the value
				if (state.equals("T") || state.equals("t")) {
					// check if we have a misorediction
					if (!ispredictedTaken) {
						mispredicted++;
					}
					// check if it's a max state
					if (defaultArray[BHRValue] < 7) {
						defaultArray[BHRValue] = defaultArray[BHRValue]+1;
					}
					//System.out.println("pre shift "+ BHR);
					BHR = shiftRight(BHR);
					//System.out.println("post shift "+ BHR);
					BHR = "1"+BHR;
					//System.out.println("post cont "+ BHR);
				} else {
					if (ispredictedTaken) {
						mispredicted++;
					}
					// check if it's a min state
					if (defaultArray[BHRValue] > 0) {
						defaultArray[BHRValue] = defaultArray[BHRValue]-1;
					}
					BHR = shiftRight(BHR);
					BHR =  "0"+BHR;
				}

			}
			System.out.println("number of predictions: " + numprediction);
			System.out.println("number of mispredictions: " + mispredicted);
			NumberFormat formatter = new DecimalFormat("#0.00");
			double rate = 0.0;
			rate = ((double)mispredicted)/((double)numprediction);
			System.out.println("Misprediction rate: " + formatter.format(rate*100)+"%");
			System.out.println("FINAL GSHARED CONTENT: ");
			// System.out.println("goodprediction " + goodprediction);
			// System.out.println("sum " + (goodprediction+mispredicted));
			for (int i = 0; i < defaultArray.length; i++) {
				System.out.println(i + " : " + defaultArray[i]);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void GeneralHybrid(String[] args) {

		int k = Integer.parseInt(args[1]);
		gpcBits = Integer.parseInt(args[2]);
		gnumBits = Integer.parseInt(args[3]);
		bnumBits = Integer.parseInt(args[4]);
		String file = args[5];	
		bpcBits = 3;
		Scanner scanner;
		gdefaultArray = new int[powerOf2(gpcBits)];
		bdefaultArray = new int[powerOf2(bnumBits)];
		chooser = new int[powerOf2(k)];

		x = (int) Math.ceil(gpcBits/4.0) + 1;
		y = (int)Math.ceil(k/4.0)+1;
		
		for (int i = 0; i < gnumBits; i++) {
			gBHR = gBHR + "0";
		}
		for (int i = 0; i < bnumBits; i++) {
			bBHR = bBHR + "0";
		}
		for (int i = 0; i < powerOf2(gpcBits); i++) {
			gdefaultArray[i] = 4;
		}
		for (int i = 0; i < powerOf2(bnumBits); i++) {
			bdefaultArray[i] = 4;
		}
		for (int i = 0; i < powerOf2(k); i++) {
			chooser[i] = 1;
		}
		try {
			scanner = new Scanner(new File(file));
			scanner.useDelimiter(" ");
			// while (scanner.hasNextLine()) {
			while (scanner.hasNextLine()) {
				int gpred = 0;
				int bpred = 0;
				String s[] = scanner.nextLine().split(" ");
				address = s[0];
				state = s[1];

				gpred = GShared(address, state);
				bpred = biModal(address, state);
				String choosertemp = "";
				String choosernewtemp = "";
				
				for (int i =(address.length()-y); i<address.length(); i ++) {
					choosertemp = choosertemp+address.charAt(i);
				}
				String chooserbinAd = Integer.toBinaryString(Integer.parseInt(choosertemp, 16));
				while (chooserbinAd.length() <= k+2) {
					chooserbinAd = "0"+chooserbinAd;
				}
				numprediction++;
				for (int i = (chooserbinAd.length()-k-2); i<chooserbinAd.length()-2; i++) {
					choosernewtemp = choosernewtemp + chooserbinAd.charAt(i);
				}
				while (choosernewtemp.length() < k) {
					choosernewtemp = "0"+choosernewtemp;
				}
				chooserBHRValue = Integer.parseInt(choosernewtemp,2);
				
				if (chooser[chooserBHRValue] > 1) {
					if (gpred == 1) {
						predictTaken++;
						ispredictedTaken=true;
					}else {
						predictNotTaken++;
						ispredictedTaken=false;
					}
					updateGShared(state, 1);
				}else {
					if (bpred == 1) {
						predictTaken++;
						ispredictedTaken=true;
					}else {
						predictNotTaken++;
						ispredictedTaken=false;
					}
					updateBimodal(state);
					updateGShared(state, 0);
				}

				if (gpred == 1 && bpred == 0) {
					if (state.equals("T") || state.equals("t")) {
						if (chooser[chooserBHRValue] < 3) {
							chooser[chooserBHRValue] = chooser[chooserBHRValue]+1;
						}
						if (!ispredictedTaken) {
							mispredicted ++;
						}
					}else {
						if (chooser[chooserBHRValue] > 0) {
							chooser[chooserBHRValue] = chooser[chooserBHRValue]-1;
						}
						if (ispredictedTaken) {
							mispredicted ++;
						}
					}
				}
				if (gpred == 0 && bpred == 1) {
					if (state.equals("N") || state.equals("n")) {
						if (chooser[chooserBHRValue] < 3) {
							chooser[chooserBHRValue] = chooser[chooserBHRValue]+1;
						}
						if (ispredictedTaken) {
							mispredicted ++;
						}
					}else {
						if (chooser[chooserBHRValue] > 0) {
							chooser[chooserBHRValue] = chooser[chooserBHRValue]-1;
						}
						if (!ispredictedTaken) {
							mispredicted ++;
						}
					}
				}
				if ((gpred == 0 && bpred == 0) || (gpred == 1 && bpred == 1)) {
					if (state.equals("T") || state.equals("t")) {
						if (!ispredictedTaken) {
							mispredicted ++;
						}
					}else {
						if (ispredictedTaken) {
							mispredicted ++;
						}
					}
				}


			}

			System.out.println("Num run " + numprediction);
			System.out.println("mispredicted " + mispredicted);
			NumberFormat formatter = new DecimalFormat("#0.00");
			double rate = 0.0;
			rate = ((double)mispredicted)/((double)numprediction);
			System.out.println("Misprediction rate: " + formatter.format(rate*100)+"%");
			System.out.println("FINAL CHOOSER CONTENT: ");
			 for (int i = 0; i < gdefaultArray.length; i++) {
			 System.out.println(i + " : " + gdefaultArray[i]);
			 }
			System.out.println("FINAL BIMODAL CONTENT: ");
			for (int i = 0; i < bdefaultArray.length; i++) {
				 System.out.println(i + " : " + bdefaultArray[i]);
				 }
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static int biModal(String address, String state) {
		temp = "";
		String newtemp = "";
		for (int i = (address.length() - x); i < address.length(); i++) {
			temp = temp + address.charAt(i);
		}
		String binAd = Integer.toBinaryString(Integer.parseInt(temp, 16));
		while (binAd.length() <= bnumBits + 2) {
			binAd = "0" + binAd;
		}
		for (int i = (binAd.length() - bnumBits - 2); i < binAd.length() - 2; i++) {
			newtemp = newtemp + binAd.charAt(i);
		}
		while (newtemp.length() < bnumBits) {
			newtemp = "0" + newtemp;
		}
		bBHRValue = Integer.parseInt(newtemp, 2);
		// check if we are predicting it
		if (bdefaultArray[bBHRValue] > 3) {
			// we predict the branch to be taken
			bispredictedTaken = true;
		} else {
			// we predict the branch to not be taken
			bispredictedTaken = false;
		}
		// increment or decrement the value
		if (bispredictedTaken) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public static void updateBimodal(String state) {
		if (state.equals("T") || state.equals("t")) {
			// check if it's a max state
			if (bdefaultArray[bBHRValue] < 7) {
				bdefaultArray[bBHRValue] = bdefaultArray[bBHRValue] + 1;
			}
			bBHR = shiftLeft(bBHR);
			bBHR = bBHR + "1";
		} else {
			// check if it's a min state
			if (bdefaultArray[bBHRValue] > 0) {
				bdefaultArray[bBHRValue] = bdefaultArray[bBHRValue] - 1;
			}
			bBHR = shiftLeft(bBHR);
			bBHR = bBHR + "0";
		}
	}
	
	public static void updateGShared(String state, int wasSelected) {
		if (state.equals("T") || state.equals("t")) {
			// check if it's a max state
			if(wasSelected == 1) {
			if (gdefaultArray[gBHRValue] < 7) {
				gdefaultArray[gBHRValue] = gdefaultArray[gBHRValue] + 1;
			}
			}
			// System.out.println("pre shift "+ BHR);
			gBHR = shiftRight(gBHR);
			// System.out.println("post shift "+ BHR);
			gBHR = "1" + gBHR;
			// System.out.println("post cont "+ BHR);
		} else {
			if(wasSelected == 1) {
			// check if it's a min state
			if (gdefaultArray[gBHRValue] > 0) {
				gdefaultArray[gBHRValue] = gdefaultArray[gBHRValue] - 1;
			}
			}
			gBHR = shiftRight(gBHR);
			gBHR = "0" + gBHR;
		}
		
		
	}

	public static int GShared(String address, String state) {
		temp = "";
		String newtemp = "";
		String mn = "";
		String xored = "";
		String concat = "";

		for (int i = (address.length() - x); i < address.length(); i++) {
			temp = temp + address.charAt(i);
		}
		String binAd = Integer.toBinaryString(Integer.parseInt(temp, 16));
		while (binAd.length() <= gpcBits + 2) {
			binAd = "0" + binAd;
		}
		
		for (int i = (binAd.length() - gpcBits - 2); i < binAd.length() - gnumBits - 2; i++) {
			mn = mn + binAd.charAt(i);
		}
		while (mn.length() < gpcBits - gnumBits) {
			mn = "0" + mn;
		}
		for (int i = (binAd.length() - gnumBits - 2); i < binAd.length() - 2; i++) {
			newtemp = newtemp + binAd.charAt(i);
		}
		while (newtemp.length() < gnumBits) {
			newtemp = "0" + newtemp;
		}
		xored = XOR(newtemp, gBHR);
		concat = mn + xored;
		// create a value for arrays
		gBHRValue = Integer.parseInt(concat, 2);
		// check if we are predicting it
		if (gdefaultArray[gBHRValue] > 3) {
			// we predict the branch to be taken
			gispredictedTaken = true;
		} else {
			// we predict the branch to not be taken
			gispredictedTaken = false;
		}
		// increment or decrement the value
		if (gispredictedTaken) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public static String shiftLeft(String binary) {
		String newBin = "";
		for (int i = 1; i < binary.length(); i++) {
			newBin = newBin + binary.charAt(i);
		}
		return newBin;
	}
	
	public static String shiftRight(String binary) {
		String newBin = "";
		//for (int i = 1; i < binary.length(); i++) {
		//	newBin = newBin + binary.charAt(i);
		//}
        for( int i =binary.length()-2; i >= 0 ; i-- )
        	newBin = binary.charAt(i) + newBin;
		return newBin;
	}

	public static int powerOf2(int num) {
		int finalNum = 1;
		for (int i = 0; i < num; i++) {
			finalNum = finalNum * 2;
		}
		return finalNum;
	}

	public static String XOR(String bin1, String bin2) {
		String xorbin = "";
		for (int i = 0; i < bin1.length(); i++) {
			if (bin1.charAt(i)=='1' && bin2.charAt(i) == '1') {
				xorbin = xorbin + "0";
			}
			if (bin1.charAt(i)=='0' && bin2.charAt(i) == '1') {
				xorbin = xorbin + "1";
			}
			if (bin1.charAt(i)=='1' && bin2.charAt(i) == '0') {
				xorbin = xorbin + "1";
			}
			if (bin1.charAt(i)=='0' && bin2.charAt(i) == '0') {
				xorbin = xorbin + "0";
			}
		}
		return xorbin;
	}
	
}
