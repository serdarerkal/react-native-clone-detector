export type CloneDetectionResult = {
  isCloned: boolean;
  score: number;
  signals: {
    sandbox: boolean;
    userId: number;
    knownCloner: boolean;
  };
};

export function DetectClone(): Promise<CloneDetectionResult>;
