
public class CalcThreshold {
	/**
	 * Calculates threshold from histogram
	 * @param histogram
	 * @param total - image height multiply by width multiply by channels
	 * @return
	 */
	public static int calc(int[] histogram, int total) {
		 
		        float sum = 0;
		        for(int i=0; i<256; i++) sum += i * histogram[i];
		 
		        float sumB = 0;
		        int wB = 0;
		        int wF = 0;
		 
		        float varMax = 0;
		        int threshold = 0;
		 
		        for(int i=0 ; i<256 ; i++) {
		            wB += histogram[i];
		            if(wB == 0) continue;
		            wF = total - wB;
		 
		            if(wF == 0) break;
		 
		            sumB += (float) (i * histogram[i]);
		            float mB = sumB / wB;
		            float mF = (sum - sumB) / wF;
		 
		            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);
		 
		            if(varBetween > varMax) {
		                varMax = varBetween;
		                threshold = i;
		            }
		        }
		 
		        return threshold;
		 
		    }

}
