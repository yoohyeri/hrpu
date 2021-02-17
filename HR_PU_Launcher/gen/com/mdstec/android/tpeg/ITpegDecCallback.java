/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\yhr\\Project\\HPRU\\APP_Src\\HR_PU_Launcher\\src\\com\\mdstec\\android\\tpeg\\ITpegDecCallback.aidl
 */
package com.mdstec.android.tpeg;
public interface ITpegDecCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.mdstec.android.tpeg.ITpegDecCallback
{
private static final java.lang.String DESCRIPTOR = "com.mdstec.android.tpeg.ITpegDecCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.mdstec.android.tpeg.ITpegDecCallback interface,
 * generating a proxy if needed.
 */
public static com.mdstec.android.tpeg.ITpegDecCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.mdstec.android.tpeg.ITpegDecCallback))) {
return ((com.mdstec.android.tpeg.ITpegDecCallback)iin);
}
return new com.mdstec.android.tpeg.ITpegDecCallback.Stub.Proxy(obj);
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
case TRANSACTION_eventCallback:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
this.eventCallback(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.mdstec.android.tpeg.ITpegDecCallback
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
@Override public void eventCallback(byte[] data, int eventCode, int subType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(data);
_data.writeInt(eventCode);
_data.writeInt(subType);
mRemote.transact(Stub.TRANSACTION_eventCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_eventCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void eventCallback(byte[] data, int eventCode, int subType) throws android.os.RemoteException;
}
