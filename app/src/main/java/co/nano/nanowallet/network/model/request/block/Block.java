package co.nano.nanowallet.network.model.request.block;

import com.google.gson.annotations.SerializedName;

import co.nano.nanowallet.network.model.BlockTypes;

/**
 * Base block
 */
public class Block {
    @SerializedName("work")
    protected String work;

    private transient BlockTypes internal_block_type;

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public BlockTypes getInternal_block_type() {
        return internal_block_type;
    }

    public void setInternal_block_type(BlockTypes internal_block_type) {
        this.internal_block_type = internal_block_type;
    }
}
