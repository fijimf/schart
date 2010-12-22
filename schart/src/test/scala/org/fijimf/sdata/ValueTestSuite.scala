package org.fijimf.schart.sdata

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.fijimf.sdata.Value
import math.BigInt
import math.BigDecimal
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ValueTestSuite extends FunSuite {
   test("Any numeric type should be a value"){
     val i=Value(1)
     val l=Value(1L)
     val f=Value(1.0f)
     val d=Value(1.0)
     val bi=Value(BigInt(1))
     val bd=Value(BigDecimal(1))
   }
}