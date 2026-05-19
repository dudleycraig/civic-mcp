export const scaleCoords = (coords, scale = 0.5, offset = [24, -29]) => {
  return [(coords[0] - offset[0]) * scale, 0, (coords[1] - offset[1]) * scale * -1];
};