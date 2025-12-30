import { NativeModules, Platform } from "react-native";

export type CloneDetectionResult = {
  isCloned: boolean;
  score: number;
  signals: {
    sandbox: boolean;
    userId: number;
    knownCloner: boolean;
  };
};

const DEFAULT_RESULT: CloneDetectionResult = {
  isCloned: false,
  score: 0,
  signals: {
    sandbox: false,
    userId: 0,
    knownCloner: false,
  },
};

const NativeCloneDetector =
  Platform.OS === "android" ? NativeModules.CloneDetectorModule : null;

/**
 * Public API
 * This is the function that consumer apps will call.
 */
export async function DetectClone(): Promise<CloneDetectionResult> {
  // Non-Android platforms always return a safe default result
  if (Platform.OS !== "android") {
    return DEFAULT_RESULT;
  }

  // Android but native module is missing or not linked
  if (
    !NativeCloneDetector ||
    typeof NativeCloneDetector.detectClone !== "function"
  ) {
    throw new Error(
      "CloneDetector native module is not linked. Make sure the Android build completed successfully."
    );
  }

  try {
    const result = await NativeCloneDetector.detectClone();

    if (
      typeof result !== "object" ||
      typeof result.isCloned !== "boolean" ||
      typeof result.score !== "number" ||
      typeof result.signals !== "object"
    ) {
      throw new Error("Invalid response from CloneDetector native module.");
    }

    return result as CloneDetectionResult;
  } catch (error) {
    throw new Error(
      `DetectClone failed: ${
        error instanceof Error ? error.message : String(error)
      }`
    );
  }
}
