﻿using Spang_PC_C_sharp;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.IO;

namespace SpangUnitTest_C_sharp
{
    
    
    /// <summary>
    ///This is a test class for MouseLeftClickerTest and is intended
    ///to contain all MouseLeftClickerTest Unit Tests
    ///</summary>
    [TestClass()]
    public class MouseLeftClickerTest
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
        ///A test for Decode
        ///</summary>
        [TestMethod()]
        public void DecodeTest()
        {
            MouseLeftClicker target = new MouseLeftClicker(); // TODO: Initialize to an appropriate value
            BinaryReader reader = null; // TODO: Initialize to an appropriate value
            target.Decode(reader);
            Assert.Inconclusive("A method that does not return a value cannot be verified.");
        }

        /// <summary>
        ///A test for MouseLeftClicker Constructor
        ///</summary>
        [TestMethod()]
        public void MouseLeftClickerConstructorTest()
        {
            MouseLeftClicker target = new MouseLeftClicker();
            Assert.Inconclusive("TODO: Implement code to verify target");
        }
    }
}