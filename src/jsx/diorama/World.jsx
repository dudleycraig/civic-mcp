import React, { useMemo } from 'react';
import { WardGeometry } from './components/WardGeometry';
import { DataBar } from './components/DataBar';

export const World = ({ data }) => {
  const wards = useMemo(() => data?.wards || [], [data])

  return (
    <group name="za-world">
      {wards.map((ward) => (
        <group key={ward.id} position={ward.position || [0, 0, 0]}>
          <WardGeometry geometry={ward.geometry} color={ward.color || '#666'} />
          {ward.stats && (
            <DataBar value={ward.stats.value} max={ward.stats.max} color={ward.stats.color} />
          )}
        </group>
      ))}
    </group>
  );
};
