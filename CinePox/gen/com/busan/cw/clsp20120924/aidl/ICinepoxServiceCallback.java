/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/CINEPOX/git/CinePox/CinePox/src/com/busan/cw/clsp20120924/aidl/ICinepoxServiceCallback.aidl
 */
package com.busan.cw.clsp20120924.aidl;
public interface ICinepoxServiceCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback
{
private static final java.lang.String DESCRIPTOR = "com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback interface,
 * generating a proxy if needed.
 */
public static com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback))) {
return ((com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback)iin);
}
return new com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_doActivity:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
this.doActivity(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void doActivity(int what, java.lang.String data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(what);
_data.writeString(data);
mRemote.transact(Stub.TRANSACTION_doActivity, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_doActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void doActivity(int what, java.lang.String data) throws android.os.RemoteException;
}
