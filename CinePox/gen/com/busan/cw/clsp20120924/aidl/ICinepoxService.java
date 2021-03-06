/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/CINEPOX/git/CinePox/CinePox/src/com/busan/cw/clsp20120924/aidl/ICinepoxService.aidl
 */
package com.busan.cw.clsp20120924.aidl;
public interface ICinepoxService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.busan.cw.clsp20120924.aidl.ICinepoxService
{
private static final java.lang.String DESCRIPTOR = "com.busan.cw.clsp20120924.aidl.ICinepoxService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.busan.cw.clsp20120924.aidl.ICinepoxService interface,
 * generating a proxy if needed.
 */
public static com.busan.cw.clsp20120924.aidl.ICinepoxService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.busan.cw.clsp20120924.aidl.ICinepoxService))) {
return ((com.busan.cw.clsp20120924.aidl.ICinepoxService)iin);
}
return new com.busan.cw.clsp20120924.aidl.ICinepoxService.Stub.Proxy(obj);
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
com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback _arg0;
_arg0 = com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback _arg0;
_arg0 = com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.busan.cw.clsp20120924.aidl.ICinepoxService
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
@Override public void doService(int what, java.lang.String data) throws android.os.RemoteException
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
@Override public void registerCallback(com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback cb) throws android.os.RemoteException
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
@Override public void unregisterCallback(com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback cb) throws android.os.RemoteException
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
public void registerCallback(com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback cb) throws android.os.RemoteException;
public void unregisterCallback(com.busan.cw.clsp20120924.aidl.ICinepoxServiceCallback cb) throws android.os.RemoteException;
}
