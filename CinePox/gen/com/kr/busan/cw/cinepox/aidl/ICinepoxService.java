/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/CINEPOX/git/CinePox/CinePox/src/com/kr/busan/cw/cinepox/aidl/ICinepoxService.aidl
 */
package com.kr.busan.cw.cinepox.aidl;
public interface ICinepoxService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.kr.busan.cw.cinepox.aidl.ICinepoxService
{
private static final java.lang.String DESCRIPTOR = "com.kr.busan.cw.cinepox.aidl.ICinepoxService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.kr.busan.cw.cinepox.aidl.ICinepoxService interface,
 * generating a proxy if needed.
 */
public static com.kr.busan.cw.cinepox.aidl.ICinepoxService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.kr.busan.cw.cinepox.aidl.ICinepoxService))) {
return ((com.kr.busan.cw.cinepox.aidl.ICinepoxService)iin);
}
return new com.kr.busan.cw.cinepox.aidl.ICinepoxService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
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
case TRANSACTION_doService:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
this.doService(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.kr.busan.cw.cinepox.aidl.ICinepoxServiceCallback _arg0;
_arg0 = com.kr.busan.cw.cinepox.aidl.ICinepoxServiceCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
com.kr.busan.cw.cinepox.aidl.ICinepoxServiceCallback _arg0;
_arg0 = com.kr.busan.cw.cinepox.aidl.ICinepoxServiceCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.kr.busan.cw.cinepox.aidl.ICinepoxService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void doService(int what, java.lang.String data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(what);
_data.writeString(data);
mRemote.transact(Stub.TRANSACTION_doService, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void registerCallback(com.kr.busan.cw.cinepox.aidl.ICinepoxServiceCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void unregisterCallback(com.kr.busan.cw.cinepox.aidl.ICinepoxServiceCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_doService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public void doService(int what, java.lang.String data) throws android.os.RemoteException;
public void registerCallback(com.kr.busan.cw.cinepox.aidl.ICinepoxServiceCallback cb) throws android.os.RemoteException;
public void unregisterCallback(com.kr.busan.cw.cinepox.aidl.ICinepoxServiceCallback cb) throws android.os.RemoteException;
}
