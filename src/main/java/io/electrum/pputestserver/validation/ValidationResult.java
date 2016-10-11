package io.electrum.pputestserver.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
   private boolean isValid = true;
   private ArrayList<RequestMessageViolation> violations = new ArrayList<>();

   public ValidationResult() {
   }

   public boolean isValid() {
      return isValid;
   }

   public void setValid(boolean valid) {
      isValid = valid;
   }

   public List<RequestMessageViolation> getViolations() {
      return violations;
   }

   public void addViolation(RequestMessageViolation reason) {
      this.setValid(false);
      violations.add(reason);

   }
}
