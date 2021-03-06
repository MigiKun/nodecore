// VeriBlock NodeCore
// Copyright 2017-2019 Xenios SEZC
// All rights reserved.
// https://www.veriblock.org
// Distributed under the MIT software license, see the accompanying
// file LICENSE or http://www.opensource.org/licenses/mit-license.php.

package nodecore.api.ucp.arguments;

import org.veriblock.core.types.Bitflag;

public class UCPArgumentBitflag extends UCPArgument {

    private final UCPArgument.UCPType type = UCPType.BITFLAG;

    private final String data;

    public void throwValidationError(String data) {
        throw new IllegalArgumentException("\"" + data + "\" did not pass the preliminary validation of "
                + getClass().getCanonicalName() + " (" + type.getPreliminaryValidationPattern() + ")");
    }

    /**
     * Constructor for parsing the serialized data type, useful when parsing a command.
     * @param data
     */
    public UCPArgumentBitflag(String data) {
        if (data == null) {
            throw new IllegalArgumentException(getClass().getCanonicalName() + "'s constructor cannot be called with null data!");
        }

        // Check the data against the initial sanity checks built into the type enum
        if (!type.preliminaryValidation(data)) {
            throwValidationError(data);
        }

        this.data = data;
    }

    /**
     * Constructor for the actual represented data type, useful when creating a command.
     * @param data
     */
    public UCPArgumentBitflag(Bitflag data) {
        String serialized = "" + data;

        // Serializing it and then checking it against preliminaryValidation ensures consistency with the rules defined in the enum
        if (!type.preliminaryValidation(serialized)) {
            throwValidationError(serialized);
        }

        this.data = data.toString();
    }

    /**
     * Gets the processed data contained within this argument, which has a Java type appropriate to the UCP type represented by this argument.
     *
     * @return The processed data
     */
    public Bitflag getData() {
        return new Bitflag(data);
    }


    /**
     * Gets the serialized version of this string which could be used to create an identical copy of this object.
     * @return The original data "sent over the wire" used to create this argument, or the serialized version created for sending over the wire.
     */
    @Override
    public String getSerialized() {
        return "" + data;
    }

    /**
     * Get the corresponding UCP type which this class represents
     * @return The corresponding UCP type represented by this argument implementation
     */
    @Override
    public UCPType getType() {
        return type;
    }

    /**
     * Gets the string representation of this argument's data: passthrough to .toString() for the underlying datatype
     * of the processed data (or the equivalent of the autoboxed version's toString if the processed data is a primitive).
     * @return String representation of the data represented by this argument
     */
    @Override
    public String toString() {
        return data.toString();
    }
}
