TestCase("myLibTest", {
    "test isEven()":function () {
        jstestdriver.console.log("JsTestDriver", "Hello World!");
        assertTrue("0 is even", isEven(0));
        assertTrue("2 is even", isEven(2));
        assertFalse("3 isn't even", isEven(3));
        console.log("Browser", "Hello World!");
    }
});

test("isEven()", function () {
    ok(isEven(0), 'Zero is an even number');
        ok(isEven(2), 'So is two');
        ok(isEven(-4), 'So is negative four');
        ok(!isEven(1), 'One is not an even number');
        ok(isEven(-7), 'Neither is negative seven');
});
