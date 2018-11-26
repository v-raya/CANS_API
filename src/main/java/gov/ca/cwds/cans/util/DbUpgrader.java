package gov.ca.cwds.cans.util;

import java.util.LinkedList;
import java.util.Queue;
import lombok.extern.slf4j.Slf4j;

/** @author denys.davydov */
@Slf4j
public final class DbUpgrader {

  private final Queue<Runnable> jobs;

  private DbUpgrader(Queue<Runnable> jobsQueue) {
    this.jobs = new LinkedList<>(jobsQueue);
  }

  public void upgradeDb() {
    while (!jobs.isEmpty()) {
      jobs.poll().run();
    }
  }

  public static DbUpgraderBuilder getBuilder() {
    return new DbUpgraderBuilder();
  }

  public static class DbUpgraderBuilder {
    Queue<Runnable> jobs = new LinkedList<>();

    public DbUpgraderBuilder add(Runnable job) {
      jobs.add(job);
      return this;
    }

    public DbUpgrader build() {
      return new DbUpgrader(jobs);
    }
  }
}
