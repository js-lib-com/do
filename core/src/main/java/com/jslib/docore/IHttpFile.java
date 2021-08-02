package com.jslib.docore;

import java.net.URI;
import java.time.LocalDateTime;

public interface IHttpFile
{

  URI getURI();

  String getName();

  LocalDateTime getModificationTime();

  int getSize();

  boolean isAfter(IHttpFile other);

}