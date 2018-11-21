package gov.ca.cwds.cans.util;

import gov.ca.cwds.cans.util.DbUpgrader.DbUpgraderBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author CWDS TPT-2 Team
 */
public class DbUpgraderTest {

  @Test
  public void upgradeDbCallQueueTest() {
    DbUpgraderBuilder builder = DbUpgrader.getBuilder();

    final boolean[] firstCalled = {false};
    final boolean[] secondCalled = {false};
    final boolean[] thirdCalled = {false};

    builder
        .add(
            (DbUpgradeJob) () -> {
              firstCalled[0] = true;
              Assert.assertFalse(secondCalled[0]);
              Assert.assertFalse(thirdCalled[0]);
            })
        .add(
            (DbUpgradeJob) () -> {
              secondCalled[0] = true;
              Assert.assertTrue(firstCalled[0]);
              Assert.assertFalse(thirdCalled[0]);
            })
        .add(
            (DbUpgradeJob) () -> {
              thirdCalled[0] = true;
              Assert.assertTrue(firstCalled[0]);
              Assert.assertTrue(secondCalled[0]);
            })
        .build()
        .upgradeDb();
  }
}
