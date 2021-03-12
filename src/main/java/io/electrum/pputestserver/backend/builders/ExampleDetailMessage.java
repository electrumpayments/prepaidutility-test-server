package io.electrum.pputestserver.backend.builders;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExampleDetailMessage {

   @JsonProperty
   private String field1 = "this is";

   @JsonProperty
   private String field2 = "an example";

   @JsonProperty
   private String field3 = "detail";

   @JsonProperty
   private String field4 = "message";

   public ExampleDetailMessage() {
      field1 = "this is";
      field2 = "an example";
      field3 = "detail";
      field4 = "message";
   }

   public String getField1() {
      return field1;
   }

   public String getField2() {
      return field2;
   }

   public String getField3() {
      return field3;
   }

   public String getField4() {
      return field4;
   }

   public void setField1(String field1) {
      this.field1 = field1;
   }

   public void setField2(String field2) {
      this.field2 = field2;
   }

   public void setField3(String field3) {
      this.field3 = field3;
   }

   public void setField4(String field4) {
      this.field4 = field4;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ExampleDetailMessage that = (ExampleDetailMessage) o;
      return Objects.equals(getField1(), that.getField1()) && Objects.equals(getField2(), that.getField2())
            && Objects.equals(getField3(), that.getField3()) && Objects.equals(getField4(), that.getField4());
   }

   @Override
   public int hashCode() {
      return Objects.hash(getField1(), getField2(), getField3(), getField4());
   }
}
