package com.spice.result;

import lombok.Data;
import lombok.experimental.Accessors;

import static com.spice.result.ResultConstant.*;

/**
 * @author spice
 * @date 2021/06/03 3:00
 */
@Data
@Accessors(chain = true)
public class CommonResult<T> {

    /**
     * 是否成功
     */
    private boolean isSuccess;

    /**
     * 状态码
     */
    private int code;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    public CommonResult() {
    }

    public CommonResult(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

    public CommonResult(boolean isSuccess, int code, String message, T data) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <E> CommonResult<E> operateSuccess() {
        return new CommonResult<E>(true, SUCCESS_CODE, OPERATE_SUCCESS_MESSAGE);
    }

    public static <E> CommonResult<E> operateSuccessWithMessage(String message) {
        return new CommonResult<>(true, SUCCESS_CODE, message);
    }

    public static <E> CommonResult<E> operateSuccess(E data) {
        return new CommonResult<>(true, SUCCESS_CODE, OPERATE_SUCCESS_MESSAGE, data);
    }

    public static <E> CommonResult<E> operateFail() {
        return new CommonResult<>(false, FAIL_CODE, OPERATE_FAIL_MESSAGE);
    }

    public static <E> CommonResult<E> operateFailWithMessage(String message) {
        return new CommonResult<E>(false, FAIL_CODE, message);
    }

    public static <E> CommonResult<E> operateFail(E data) {
        return new CommonResult<>(false, FAIL_CODE, OPERATE_FAIL_MESSAGE, data);
    }

    public static <E> CommonResult<E> autoResult(boolean isSuccess) {
        if (isSuccess) {
            return operateSuccess();
        } else {
            return operateFail();
        }
    }

    public static <E> CommonResult<E> autoResult(boolean isSuccess, E data) {
        if (isSuccess) {
            return CommonResult.operateSuccess(data);
        } else {
            return CommonResult.operateFail(data);
        }
    }
}
