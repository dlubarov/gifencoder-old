package com.gifencoder;

import java.util.Set;

public interface Ditherer {
  /**
   * Dither the given image, producing a new image which only contains colors from the given color
   * set.
   *
   * @param image the original, unquantized image
   * @param newColors the quantized set of colors to be used in the new image
   * @return a new image containing only of colors from {@code newColors}
   */
  Image dither(Image image, Set<Color> newColors);
}
