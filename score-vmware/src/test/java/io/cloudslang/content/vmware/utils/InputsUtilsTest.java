package io.cloudslang.content.vmware.utils;

import io.cloudslang.content.vmware.entities.VmInputs;
import io.cloudslang.content.vmware.entities.http.HttpInputs;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.Assert.*;

/**
 * Created by Mihai Tusa.
 * 1/11/2016.
 */
public class InputsUtilsTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void getIntInput() {
        int testInt = InputUtils.getIntInput("4096", 1024);
        assertEquals(4096, testInt);
    }

    @Test
    public void getIntInputDefault() {
        int testInt = InputUtils.getIntInput("", 1024);
        assertEquals(1024, testInt);
    }

    @Test
    public void getIntInputException() throws RuntimeException {
        exception.expect(RuntimeException.class);
        exception.expectMessage("The input value must be a int number.");

        InputUtils.getIntInput("Doesn't work in this way", 0);
    }

    @Test
    public void getLongInput() {
        long testLong = InputUtils.getLongInput("4096", 1024L);
        assertEquals(4096L, testLong);
    }

    @Test
    public void getLongInputDefault() {
        long testLong = InputUtils.getLongInput("", 1024L);
        assertEquals(1024L, testLong);
    }

    @Test
    public void getLongInputException() throws RuntimeException {
        exception.expect(RuntimeException.class);
        exception.expectMessage("The input value must be a long number.");

        InputUtils.getLongInput("Still doesn't work in this way", 0);
    }

    @Test
    public void getUrlStringSuccess() throws Exception {
        HttpInputs httpInputs = new HttpInputs.HttpInputsBuilder()
                .withHost("vc6.subdomain.example.com")
                .withPort("443")
                .withProtocol("")
                .withUsername("")
                .withPassword("")
                .withTrustEveryone("false").build();
        String testUrl = InputUtils.getUrlString(httpInputs);

        assertEquals("https://vc6.subdomain.example.com:443/sdk", testUrl);
    }

    @Test
    public void getUrlStringException() throws Exception {
        exception.expect(Exception.class);
        exception.expectMessage("Unsupported protocol value: [myProtocol]. Valid values are: https, http.");

        HttpInputs httpInputs = new HttpInputs.HttpInputsBuilder()
                .withHost("")
                .withPort("8080")
                .withProtocol("myProtocol")
                .withUsername("")
                .withPassword("")
                .withTrustEveryone("true").build();
        InputUtils.getUrlString(httpInputs);
    }

    @Test
    public void getSuccessfullyDefaultDelimiter() {
        String testDelimiter = InputUtils.getDefaultDelimiter("", ",");

        assertEquals(",", testDelimiter);
    }

    @Test
    public void getDiskFileNameString() {
        String testDiskFileNameString = InputUtils.getDiskFileNameString("someDataStore", "testVM", "Renamed");

        assertEquals("[someDataStore] testVM/Renamed.vmdk", testDiskFileNameString);
    }

    @Test
    public void checkValidOperation() throws Exception {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Invalid operation specified for disk device. The disk device can be only added or removed.");

        VmInputs vmInputs = new VmInputs.VmInputsBuilder()
                .withDevice("disk")
                .withOperation("update")
                .build();
        InputUtils.checkValidOperation(vmInputs, "disk");
    }

    @Test
    public void isUpdateOperation() throws Exception {
        VmInputs vmInputs = new VmInputs.VmInputsBuilder()
                .withOperation("add")
                .build();
        boolean isUpdate = InputUtils.isUpdateOperation(vmInputs);

        assertFalse(isUpdate);
    }

    @Test
    public void isIntFalse() {
        boolean isUpdate = InputUtils.isInt("2147483648");

        assertFalse(isUpdate);
    }

    @Test
    public void isIntTrue() {
        boolean isUpdate = InputUtils.isInt("2147483647");

        assertTrue(isUpdate);
    }

    @Test
    public void validateDiskInputsAdd() throws Exception {
        exception.expect(RuntimeException.class);
        exception.expectMessage("The disk size must be positive long.");

        VmInputs vmInputs = new VmInputs.VmInputsBuilder()
                .withVirtualMachineName("testVM")
                .withOperation("add")
                .withDevice("disk")
                .withLongVmDiskSize("0")
                .withDiskMode("persistent")
                .build();
        InputUtils.validateDiskInputs(vmInputs);
    }

    @Test
    public void validateDiskInputsRemove() throws Exception {
        exception.expect(RuntimeException.class);
        exception.expectMessage("The [] is not a valid disk label.");

        VmInputs vmInputs = new VmInputs.VmInputsBuilder()
                .withVirtualMachineName("testVM")
                .withOperation("remove")
                .withDevice("disk")
                .withUpdateValue("")
                .build();
        InputUtils.validateDiskInputs(vmInputs);
    }
}