package com.fousalert.commonconstants;


public class Constants {

	public enum TickerHistoryDuration {
		ONE_MINUTE(1),FIVE_MINUTE(5);
		
		private int duration;
		private TickerHistoryDuration(int duration) {
			this.duration = duration;
		}
		public int getDuration() {
			return duration;
		}
	}
	
	public enum ResultStatus {
		SUCCESS("Success"), ERROR("Error"), WARNING("Warning");
		
		private  String displayStatus;
		
		private ResultStatus(String displayStatus) {
			this.displayStatus = displayStatus;
		}
		
		public String getDisplayStatus() {
			return displayStatus;
		}

		public int getValue() {
			return this.ordinal();
		}
		
		public static ResultStatus getByValue(int value) {
			return ResultStatus.values()[value];
		}
		
		public String getLabel() {
		   return this.toString();
		}
	}

}
