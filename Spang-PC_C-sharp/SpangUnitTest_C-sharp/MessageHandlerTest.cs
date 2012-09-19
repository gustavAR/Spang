using Spang_PC_C_sharp;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;

namespace SpangUnitTest_C_sharp
{
    
    
    /// <summary>
    ///This is a test class for MessageHandlerTest and is intended
    ///to contain all MessageHandlerTest Unit Tests
    ///</summary>
    [TestClass()]
    public class MessageHandlerTest
    {


        private TestContext testContextInstance;

        /// <summary>
        ///Gets or sets the test context which provides
        ///information about and functionality for the current test run.
        ///</summary>
        public TestContext TestContext
        {
            get
            {
                return testContextInstance;
            }
            set
            {
                testContextInstance = value;
            }
        }

        #region Additional test attributes
        // 
        //You can use the following additional attributes as you write your tests:
        //
        //Use ClassInitialize to run code before running the first test in the class
        //[ClassInitialize()]
        //public static void MyClassInitialize(TestContext testContext)
        //{
        //}
        //
        //Use ClassCleanup to run code after all tests in a class have run
        //[ClassCleanup()]
        //public static void MyClassCleanup()
        //{
        //}
        //
        //Use TestInitialize to run code before running each test
        //[TestInitialize()]
        //public void MyTestInitialize()
        //{
        //}
        //
        //Use TestCleanup to run code after each test has run
        //[TestCleanup()]
        //public void MyTestCleanup()
        //{
        //}
        //
        #endregion


        /// <summary>
        ///A test for MessageHandler Constructor
        ///</summary>
        [TestMethod()]
        public void MessageHandlerConstructorTest()
        {
            Dictionary<byte, IMessageHandler> dict = null; // TODO: Initialize to an appropriate value
            MessageHandler target = new MessageHandler(dict);
            Assert.Inconclusive("TODO: Implement code to verify target");
        }

        /// <summary>
        ///A test for DecodeMessage
        ///</summary>
        [TestMethod()]
        public void DecodeMessageTest()
        {
            Dictionary<byte, IMessageHandler> dict = null; // TODO: Initialize to an appropriate value
            MessageHandler target = new MessageHandler(dict); // TODO: Initialize to an appropriate value
            byte[] message = null; // TODO: Initialize to an appropriate value
            target.DecodeMessage(message);
            Assert.Inconclusive("A method that does not return a value cannot be verified.");
        }
    }
}
