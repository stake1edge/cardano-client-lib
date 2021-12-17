package com.bloxbean.cardano.client.transaction.spec.script;

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.UnsignedInteger;
import com.bloxbean.cardano.client.exception.CborDeserializationException;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bouncycastle.util.encoders.Hex;

import java.nio.ByteBuffer;
import java.util.List;

import static com.bloxbean.cardano.client.crypto.KeyGenUtil.blake2bHash224;

public interface NativeScript extends Script {

    static NativeScript deserialize(Array nativeScriptArray) throws CborDeserializationException {
        List<DataItem> dataItemList = nativeScriptArray.getDataItems();
        if(dataItemList == null || dataItemList.size() == 0) {
            throw new CborDeserializationException("NativeScript deserialization failed. Invalid no of DataItem");
        }

        int type = ((UnsignedInteger)dataItemList.get(0)).getValue().intValue();
        if(type == 0) {
            return ScriptPubkey.deserialize(nativeScriptArray);
        } else if(type == 1) {
            return ScriptAll.deserialize(nativeScriptArray);
        } else if(type == 2) {
            return ScriptAny.deserialize(nativeScriptArray);
        } else if(type == 3) {
            return ScriptAtLeast.deserialize(nativeScriptArray);
        } else if(type == 4) {
            return RequireTimeAfter.deserialize(nativeScriptArray);
        } else if(type ==5) {
            return RequireTimeBefore.deserialize(nativeScriptArray);
        } else {
            return null;
        }
    }

    @JsonIgnore
    default String getPolicyId() throws CborSerializationException {
        byte[] first = new byte[]{00};
        byte[] serializedBytes = this.serialize();
        byte[] finalBytes = ByteBuffer.allocate(first.length + serializedBytes.length)
                .put(first)
                .put(serializedBytes)
                .array();

        return Hex.toHexString(blake2bHash224(finalBytes));
    }

    @JsonIgnore
    default byte[] getScriptHash() throws CborSerializationException {
        byte[] first = new byte[]{00};
        byte[] serializedBytes = this.serialize();
        byte[] finalBytes = ByteBuffer.allocate(first.length + serializedBytes.length)
                .put(first)
                .put(serializedBytes)
                .array();

        return blake2bHash224(finalBytes);
    }
}
