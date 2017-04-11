package com.google.cloud.tools.gradle.appengine.core.model;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ExtZ {
  private String zz = "hello";
  private Map<String, List<String>> zzNested =
      ImmutableMap.of("a", Arrays.asList("a1", "a2"), "b", Arrays.asList("b1", "b2"));
}
