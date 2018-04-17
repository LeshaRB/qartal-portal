package com.nomis.dto;

import java.util.List;
import lombok.Data;

/**
 * Created by Eugene Tsydzik
 * on 4/14/18.
 */
@Data
public class BaselineJobs {

  private List<Baseline> queued;
  private List<Baseline> running;
}