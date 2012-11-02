/**
 * 0. Project  : CinePox_MVC
 *
 * 1. FileName : CinePoxService.java
 * 2. Package : com.busan.cw.clsp20120924.controller
 * 3. Comment : 
 * 4. 작성자  : 박찬우
 * 5. 작성일  : 2012. 10. 31. 오후 6:17:52
 * 6. 변경이력 : 
 *		2012. 10. 31. 오후 6:17:52 : 신규
 *
 */
package com.busan.cw.clsp20120924.controller;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : CinePoxService.java
 * 3. Package  : com.busan.cw.clsp20120924.controller
 * 4. Comment  : 
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 31. 오후 6:17:52
 * </PRE>
 */
public class CinePoxService extends Service {

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
