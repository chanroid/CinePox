package com.busan.cw.clsp20120924.interfaces;

import com.busan.cw.clsp20120924.model.TicketModel;

public interface TicketModelCallback {
	public void onTicketLoadStart(TicketModel model);
	public void onTicketLoadComplete(TicketModel model);
	public void onTicketLoadError(TicketModel model, String message);
}
