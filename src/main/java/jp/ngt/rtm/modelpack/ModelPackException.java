package jp.ngt.rtm.modelpack;

import javax.annotation.Nullable;

public class ModelPackException extends RuntimeException
{
	public ModelPackException(String message, String fileName, @Nullable Throwable cause)
    {
        super(message + " (" + fileName + ")", cause);
    }

    public ModelPackException(String message, String fileName)
    {
        this(message, fileName, null);
    }
}