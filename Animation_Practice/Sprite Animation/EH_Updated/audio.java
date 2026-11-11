 private void playRasenganAudio() {

        int audioID = getResources().getIdentifier(
                "rasengan", "raw", getPackageName()
        );

        if (rasenganPlayer != null) {
            rasenganPlayer.release();
            rasenganPlayer = null;
        }

        if (audioID != 0) {
            rasenganPlayer = MediaPlayer.create(this, audioID);

            if (rasenganPlayer != null) {
                rasenganPlayer.setOnCompletionListener(mp -> {
                    mp.release();
                    rasenganPlayer = null;
                });

                rasenganPlayer.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (rasenganPlayer != null) {
            rasenganPlayer.release();
            rasenganPlayer = null;
        }
    }
